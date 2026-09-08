package com.blitzlabx.zcode.core

import java.math.BigInteger

/**
 * Arbitrary-base (Base-N) encoder/decoder using a custom alphabet.
 * Used to turn binary Z-Code frames into human-readable / QR-friendly strings.
 */
object BaseN {

    fun encode(data: ByteArray, alphabet: String): String {
        if (data.isEmpty()) return ""
        val base = alphabet.length
        if (base < 2) throw IllegalArgumentException("Alphabet too short")

        // Count leading zeros
        var zeros = 0
        while (zeros < data.size && data[zeros] == 0.toByte()) zeros++

        // Convert to BigInteger (unsigned)
        val num = BigInteger(1, data)
        if (num == BigInteger.ZERO) {
            return alphabet[0].toString().repeat(zeros.coerceAtLeast(1))
        }

        val sb = StringBuilder()
        var n = num
        val baseBI = BigInteger.valueOf(base.toLong())
        while (n > BigInteger.ZERO) {
            val (div, rem) = n.divideAndRemainder(baseBI)
            sb.append(alphabet[rem.toInt()])
            n = div
        }
        // Leading zeros become leading alphabet[0]
        repeat(zeros) { sb.append(alphabet[0]) }
        return sb.reverse().toString()
    }

    fun decode(encoded: String, alphabet: String): ByteArray {
        if (encoded.isEmpty()) return ByteArray(0)
        val base = alphabet.length
        if (base < 2) throw IllegalArgumentException("Alphabet too short")

        val charMap = alphabet.withIndex().associate { it.value to it.index }
        // Leading zeros
        var zeros = 0
        while (zeros < encoded.length && encoded[zeros] == alphabet[0]) zeros++

        var num = BigInteger.ZERO
        val baseBI = BigInteger.valueOf(base.toLong())
        for (c in encoded) {
            val idx = charMap[c] ?: throw IllegalArgumentException("Character '$c' not in alphabet")
            num = num.multiply(baseBI).add(BigInteger.valueOf(idx.toLong()))
        }

        val bytes = if (num == BigInteger.ZERO) ByteArray(0) else num.toByteArray()
        // BigInteger may add a leading zero sign byte
        val stripped = if (bytes.isNotEmpty() && bytes[0] == 0.toByte()) {
            bytes.copyOfRange(1, bytes.size)
        } else bytes

        // Restore leading zeros
        return if (zeros > 0) {
            ByteArray(zeros) + stripped
        } else stripped
    }
}
