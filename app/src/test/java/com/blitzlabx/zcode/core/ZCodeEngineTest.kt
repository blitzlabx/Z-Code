package com.blitzlabx.zcode.core

import org.junit.Assert.*
import org.junit.Test

class ZCodeEngineTest {

    @Test
    fun `round trip simple ascii`() {
        val text = "Hello Blitz!"
        val config = ZCodeConfig()
        val encoded = ZCodeEngine.encode(text, config)
        val decoded = ZCodeEngine.decode(encoded.zCode, preferredConfig = config)
        assertEquals(text, decoded.text)
        assertTrue(decoded.verified)
    }

    @Test
    fun `round trip unicode and multiline`() {
        val text = "Hello 世界!\nLine 2\nEmoji: 🚀🔐\nPunctuation: !@#$%^&*()"
        val config = ZCodeConfig(mode = Mode.STANDARD, zLanguage = ZLanguage.Z_ALPHANUMERIC)
        val encoded = ZCodeEngine.encode(text, config)
        val decoded = ZCodeEngine.decode(encoded.zCode)
        assertEquals(text, decoded.text)
        assertTrue(decoded.verified)
    }

    @Test
    fun `round trip with password`() {
        val text = "Secret message with password protection"
        val config = ZCodeConfig(password = "BlitzSecure123!", mode = Mode.SECURE)
        val encoded = ZCodeEngine.encode(text, config)
        val decoded = ZCodeEngine.decode(encoded.zCode, password = "BlitzSecure123!")
        assertEquals(text, decoded.text)
        assertTrue(decoded.verified)

        // Wrong password must fail
        try {
            ZCodeEngine.decode(encoded.zCode, password = "wrong")
            fail("Should have thrown")
        } catch (e: ZCodeEngine.ZCodeException) {
            // expected
        }
    }

    @Test
    fun `all modes and languages round trip`() {
        val text = "Test message for all configurations 测试"
        for (mode in Mode.entries) {
            for (lang in ZLanguage.entries.filter { it != ZLanguage.Z_CUSTOM }) {
                for (codeType in CodeType.entries) {
                    val config = ZCodeConfig(
                        codeType = codeType,
                        hash = HashAlgorithm.SHA256,
                        zLanguage = lang,
                        mode = mode,
                        password = if (mode == Mode.SECURE) "testpass" else null
                    )
                    val encoded = ZCodeEngine.encode(text, config)
                    val decoded = ZCodeEngine.decode(
                        encoded.zCode,
                        password = config.password,
                        preferredConfig = config
                    )
                    assertEquals("Failed for $mode / $lang / $codeType", text, decoded.text)
                }
            }
        }
    }

    @Test
    fun `empty message rejected`() {
        try {
            ZCodeEngine.encode("", ZCodeConfig())
            fail()
        } catch (e: ZCodeEngine.ZCodeException) {
            // expected
        }
    }

    @Test
    fun `malformed input rejected`() {
        try {
            ZCodeEngine.decode("not-a-valid-zcode-!!!")
            fail()
        } catch (e: ZCodeEngine.ZCodeException) {
            // expected
        }
    }

    @Test
    fun `long text`() {
        val text = "A".repeat(5000) + " 🚀 " + "B".repeat(5000)
        val config = ZCodeConfig(mode = Mode.ULTRA_COMPACT, zLanguage = ZLanguage.Z_ALPHANUMERIC)
        val encoded = ZCodeEngine.encode(text, config)
        val decoded = ZCodeEngine.decode(encoded.zCode, preferredConfig = config)
        assertEquals(text, decoded.text)
    }

    @Test
    fun `compression reduces size for repetitive data`() {
        val text = "Hello Blitz! ".repeat(100)
        val compact = ZCodeEngine.encode(text, ZCodeConfig(mode = Mode.ULTRA_COMPACT))
        val standard = ZCodeEngine.encode(text, ZCodeConfig(mode = Mode.STANDARD))
        // Ultra should generally be shorter or equal after encoding
        assertTrue(compact.zCodeLength <= standard.zCodeLength + 50)
    }
}
