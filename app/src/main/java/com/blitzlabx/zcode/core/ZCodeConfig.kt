package com.blitzlabx.zcode.core

/**
 * Z-Code configuration parameters.
 * Clearly separates encoding alphabet, hashing for integrity, and optional real encryption.
 */
enum class CodeType(val id: String, val displayName: String) {
    COMPACT("compact", "Compact"),
    STANDARD("standard", "Standard"),
    EXTENDED("extended", "Extended");

    companion object {
        fun fromId(id: String): CodeType = entries.find { it.id.equals(id, true) } ?: COMPACT
    }
}

enum class HashAlgorithm(val id: String, val displayName: String, val bits: Int) {
    SHA256("SHA-256", "SHA-256", 256),
    SHA512("SHA-512", "SHA-512", 512),
    NONE("none", "None", 0);

    companion object {
        fun fromId(id: String): HashAlgorithm = entries.find { it.id.equals(id, true) } ?: SHA256
    }
}

enum class ZLanguage(val id: String, val displayName: String, val description: String, val alphabet: String) {
    Z_ALPHA(
        "z-alpha",
        "Z-Alpha",
        "Balanced • Compact • v1.0",
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    ),
    Z_NUMERIC(
        "z-numeric",
        "Z-Numeric",
        "Numeric only • Ultra Compact • v1.0",
        "0123456789"
    ),
    Z_SYMBOLIC(
        "z-symbolic",
        "Z-Symbolic",
        "Symbols • Secure • v1.0",
        "!@#\$%^&*()_+-=[]{}|;:,.<>?/~`"
    ),
    Z_ALPHANUMERIC(
        "z-alphanumeric",
        "Z-Alphanumeric",
        "Mixed • Flexible • v1.0",
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    ),
    Z_CUSTOM(
        "z-custom",
        "Z-Custom",
        "User defined • Advanced • v1.0",
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    );

    companion object {
        fun fromId(id: String): ZLanguage = entries.find { it.id.equals(id, true) } ?: Z_ALPHA
    }
}

enum class Mode(val id: String, val displayName: String, val description: String) {
    STANDARD("standard", "Standard", "Balanced speed and security"),
    COMPACT("compact", "Compact", "Smaller output size"),
    SECURE("secure", "Secure", "Encryption + hashing"),
    ULTRA_COMPACT("ultra-compact", "Ultra Compact", "Maximum compression"),
    CUSTOM("custom", "Custom", "User defined settings");

    companion object {
        fun fromId(id: String): Mode = entries.find { it.id.equals(id, true) } ?: STANDARD
    }
}

data class ZCodeConfig(
    val codeType: CodeType = CodeType.COMPACT,
    val hash: HashAlgorithm = HashAlgorithm.SHA256,
    val zLanguage: ZLanguage = ZLanguage.Z_ALPHA,
    val mode: Mode = Mode.STANDARD,
    val password: String? = null,
    val customAlphabet: String? = null
) {
    fun effectiveAlphabet(): String {
        return if (zLanguage == ZLanguage.Z_CUSTOM && !customAlphabet.isNullOrBlank()) {
            customAlphabet.filter { !it.isWhitespace() }.toSet().joinToString("")
                .ifEmpty { ZLanguage.Z_CUSTOM.alphabet }
        } else {
            zLanguage.alphabet
        }
    }

    fun requiresEncryption(): Boolean = !password.isNullOrEmpty() || mode == Mode.SECURE

    fun hashLengthBytes(): Int {
        return when {
            mode == Mode.ULTRA_COMPACT -> 4
            mode == Mode.COMPACT || codeType == CodeType.COMPACT -> 8
            hash == HashAlgorithm.NONE -> 0
            hash == HashAlgorithm.SHA512 -> 16
            else -> 12
        }
    }

    fun useCompression(): Boolean = mode == Mode.COMPACT || mode == Mode.ULTRA_COMPACT ||
            codeType == CodeType.COMPACT

    fun kdfIterations(): Int = when (mode) {
        Mode.SECURE -> 150_000
        Mode.STANDARD -> 100_000
        else -> 50_000
    }
}
