package com.blitzlabx.zcode.core

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Production Z-Code encoding / decoding engine.
 *
 * Pipeline (encode):
 * 1. UTF-8 encode input text (preserves Unicode, punctuation, multiline)
 * 2. Optional Deflate compression (mode / code-type dependent)
 * 3. Optional AES-256-GCM encryption when password present or Secure mode
 *    - Key derived via PBKDF2-HMAC-SHA256 (never store password)
 * 4. Integrity hash (SHA-256 / SHA-512 truncated) of the protected payload
 * 5. Self-describing header + payload + hash packed into binary
 * 6. Base-N encoding using the selected Z-Language alphabet
 *
 * Decoding reverses the steps with validation and round-trip safety.
 * Encoding ≠ hashing ≠ encryption; each is applied only when configured.
 */
object ZCodeEngine {

    private const val VERSION: Byte = 1
    private const val FLAG_COMPRESSED: Int = 0x01
    private const val FLAG_ENCRYPTED: Int = 0x02
    private const val FLAG_PASSWORD: Int = 0x04
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 16
    private const val SALT_LENGTH = 16
    private val MAGIC = byteArrayOf(0x5A, 0x43) // "ZC"

    private val secureRandom = SecureRandom()

    data class EncodeResult(
        val zCode: String,
        val originalLength: Int,
        val zCodeLength: Int,
        val config: ZCodeConfig
    )

    data class DecodeResult(
        val text: String,
        val originalLength: Int,
        val zCodeLength: Int,
        val detectedConfig: ZCodeConfig,
        val verified: Boolean
    )

    class ZCodeException(message: String, cause: Throwable? = null) : Exception(message, cause)

    fun encode(text: String, config: ZCodeConfig): EncodeResult {
        if (text.isEmpty()) throw ZCodeException("Message cannot be empty")
        val alphabet = config.effectiveAlphabet()
        if (alphabet.length < 2) throw ZCodeException("Alphabet must contain at least 2 unique characters")

        val utf8 = text.toByteArray(StandardCharsets.UTF_8)
        var payload = utf8

        // 1. Compression
        val compressed = if (config.useCompression()) {
            val c = compress(payload)
            if (c.size < payload.size) {
                payload = c
                true
            } else false
        } else false

        // 2. Encryption (real crypto, only when password or Secure mode)
        val encrypted: Boolean
        val salt: ByteArray?
        val iv: ByteArray?
        if (config.requiresEncryption()) {
            val pwd = config.password ?: "Z-Code-Secure-Default-Key-Do-Not-Use-In-Prod"
            val derived = deriveKey(pwd, config.kdfIterations())
            salt = derived.salt
            val enc = encrypt(payload, derived.key)
            payload = enc.ciphertext
            iv = enc.iv
            encrypted = true
        } else {
            salt = null
            iv = null
            encrypted = false
        }

        // 3. Integrity hash of the final protected payload
        val hashBytes = if (config.hash != HashAlgorithm.NONE && config.hashLengthBytes() > 0) {
            val digest = MessageDigest.getInstance(
                if (config.hash == HashAlgorithm.SHA512) "SHA-512" else "SHA-256"
            )
            digest.update(payload)
            digest.digest().copyOf(config.hashLengthBytes())
        } else ByteArray(0)

        // 4. Build binary frame: MAGIC | VERSION | FLAGS | meta | [salt][iv] | payload | hash
        val flags = (if (compressed) FLAG_COMPRESSED else 0) or
                (if (encrypted) FLAG_ENCRYPTED else 0) or
                (if (!config.password.isNullOrEmpty()) FLAG_PASSWORD else 0)

        val meta = byteArrayOf(
            config.codeType.ordinal.toByte(),
            config.hash.ordinal.toByte(),
            config.zLanguage.ordinal.toByte(),
            config.mode.ordinal.toByte()
        )

        val baos = ByteArrayOutputStream()
        baos.write(MAGIC)
        baos.write(VERSION.toInt())
        baos.write(flags)
        baos.write(meta)
        if (salt != null) baos.write(salt)
        if (iv != null) baos.write(iv)
        baos.write(payload)
        baos.write(hashBytes)
        val frame = baos.toByteArray()

        // 5. Base-N encode
        val zCode = BaseN.encode(frame, alphabet)

        return EncodeResult(
            zCode = zCode,
            originalLength = text.length,
            zCodeLength = zCode.length,
            config = config
        )
    }

    fun decode(zCode: String, password: String? = null, preferredConfig: ZCodeConfig? = null): DecodeResult {
        if (zCode.isBlank()) throw ZCodeException("Z-Code cannot be empty")

        // Try preferred alphabet first, then all known languages
        val alphabetsToTry = mutableListOf<String>()
        preferredConfig?.let { alphabetsToTry.add(it.effectiveAlphabet()) }
        ZLanguage.entries.forEach { alphabetsToTry.add(it.alphabet) }
        alphabetsToTry.add(ZLanguage.Z_CUSTOM.alphabet)

        var lastError: Exception? = null
        for (alphabet in alphabetsToTry.distinct()) {
            try {
                return decodeWithAlphabet(zCode, alphabet, password)
            } catch (e: Exception) {
                lastError = e
            }
        }
        throw ZCodeException("Unable to decode Z-Code. Invalid format, wrong alphabet, or incorrect password.", lastError)
    }

    private fun decodeWithAlphabet(zCode: String, alphabet: String, password: String?): DecodeResult {
        val frame = try {
            BaseN.decode(zCode, alphabet)
        } catch (e: Exception) {
            throw ZCodeException("Invalid characters or alphabet mismatch", e)
        }

        if (frame.size < 8) throw ZCodeException("Z-Code too short")

        var offset = 0
        if (frame[0] != MAGIC[0] || frame[1] != MAGIC[1]) {
            throw ZCodeException("Invalid Z-Code magic header")
        }
        offset += 2

        val version = frame[offset++]
        if (version != VERSION) throw ZCodeException("Unsupported Z-Code version: $version")

        val flags = frame[offset++].toInt() and 0xFF
        val compressed = (flags and FLAG_COMPRESSED) != 0
        val encrypted = (flags and FLAG_ENCRYPTED) != 0
        val hasPasswordFlag = (flags and FLAG_PASSWORD) != 0

        if (offset + 4 > frame.size) throw ZCodeException("Truncated header")
        val codeType = CodeType.entries.getOrElse(frame[offset++].toInt() and 0xFF) { CodeType.COMPACT }
        val hashAlg = HashAlgorithm.entries.getOrElse(frame[offset++].toInt() and 0xFF) { HashAlgorithm.SHA256 }
        val zLang = ZLanguage.entries.getOrElse(frame[offset++].toInt() and 0xFF) { ZLanguage.Z_ALPHA }
        val mode = Mode.entries.getOrElse(frame[offset++].toInt() and 0xFF) { Mode.STANDARD }

        val detected = ZCodeConfig(
            codeType = codeType,
            hash = hashAlg,
            zLanguage = zLang,
            mode = mode,
            password = password
        )

        var salt: ByteArray? = null
        var iv: ByteArray? = null
        if (encrypted) {
            if (offset + SALT_LENGTH > frame.size) throw ZCodeException("Missing salt")
            salt = frame.copyOfRange(offset, offset + SALT_LENGTH)
            offset += SALT_LENGTH
            if (offset + GCM_IV_LENGTH > frame.size) throw ZCodeException("Missing IV")
            iv = frame.copyOfRange(offset, offset + GCM_IV_LENGTH)
            offset += GCM_IV_LENGTH
        }

        val hashLen = detected.hashLengthBytes()
        if (offset + hashLen > frame.size) throw ZCodeException("Truncated payload/hash")

        val payloadEnd = frame.size - hashLen
        val payload = frame.copyOfRange(offset, payloadEnd)
        val storedHash = if (hashLen > 0) frame.copyOfRange(payloadEnd, frame.size) else ByteArray(0)

        // Verify integrity hash
        var verified = true
        if (hashLen > 0 && hashAlg != HashAlgorithm.NONE) {
            val digest = MessageDigest.getInstance(
                if (hashAlg == HashAlgorithm.SHA512) "SHA-512" else "SHA-256"
            )
            digest.update(payload)
            val computed = digest.digest().copyOf(hashLen)
            if (!MessageDigest.isEqual(computed, storedHash)) {
                verified = false
                // Still attempt decrypt for better error messages, but mark unverified
            }
        }

        // Decrypt
        var data = payload
        if (encrypted) {
            val pwd = password
                ?: if (hasPasswordFlag) throw ZCodeException("Password required for this Z-Code")
                else "Z-Code-Secure-Default-Key-Do-Not-Use-In-Prod"
            try {
                val key = deriveKeyWithSalt(pwd, salt!!, detected.kdfIterations())
                data = decrypt(payload, key, iv!!)
            } catch (e: Exception) {
                throw ZCodeException("Decryption failed. Wrong password or corrupted data.", e)
            }
        }

        // Decompress
        if (compressed) {
            try {
                data = decompress(data)
            } catch (e: Exception) {
                throw ZCodeException("Decompression failed. Data may be corrupted.", e)
            }
        }

        val text = String(data, StandardCharsets.UTF_8)
        return DecodeResult(
            text = text,
            originalLength = text.length,
            zCodeLength = zCode.length,
            detectedConfig = detected.copy(password = null), // never echo password
            verified = verified
        )
    }

    // ---- Crypto helpers ----

    private data class DerivedKey(val key: ByteArray, val salt: ByteArray)

    private fun deriveKey(password: String, iterations: Int): DerivedKey {
        val salt = ByteArray(SALT_LENGTH).also { secureRandom.nextBytes(it) }
        return DerivedKey(deriveKeyWithSalt(password, salt, iterations), salt)
    }

    private fun deriveKeyWithSalt(password: String, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded
    }

    private data class EncResult(val ciphertext: ByteArray, val iv: ByteArray)

    private fun encrypt(plain: ByteArray, key: ByteArray): EncResult {
        val iv = ByteArray(GCM_IV_LENGTH).also { secureRandom.nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(GCM_TAG_LENGTH * 8, iv))
        val ct = cipher.doFinal(plain)
        return EncResult(ct, iv)
    }

    private fun decrypt(ciphertext: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(GCM_TAG_LENGTH * 8, iv))
        return cipher.doFinal(ciphertext)
    }

    // ---- Compression ----

    private fun compress(data: ByteArray): ByteArray {
        val baos = ByteArrayOutputStream()
        DeflaterOutputStream(baos, Deflater(Deflater.BEST_COMPRESSION)).use { it.write(data) }
        return baos.toByteArray()
    }

    private fun decompress(data: ByteArray): ByteArray {
        return InflaterInputStream(ByteArrayInputStream(data), Inflater()).use { it.readBytes() }
    }
}
