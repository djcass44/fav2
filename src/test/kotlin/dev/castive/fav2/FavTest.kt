package dev.castive.fav2

import com.django.log2.logging.Log
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class FavTest {
    @BeforeEach
    internal fun setUp() {
        Fav.DEBUG = true
        Fav.ALLOW_HTTP = false
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "https://github.com",
        "https://google.com",
        "https://apple.com"
    ])
    fun getKnown(value: String) {
        val icon = Fav.loadDomain(value)
        Log.d(javaClass, icon)
        assertNotNull(icon)
        assertTrue(icon!!.endsWith("png") || icon.endsWith("ico"))
    }

    @Test
    fun checkSecure() {
        Fav.ALLOW_HTTP = false
        val allowed = Fav.checkDomain("https://google.com")
        assertTrue(allowed)
    }
    @Test
    fun checkInsecure() {
        Fav.ALLOW_HTTP = false
        val allowed = Fav.checkDomain("http://google.com")
        assertFalse(allowed)
    }
    @Test
    fun checkInsecureAllowed() {
        Fav.ALLOW_HTTP = true
        val allowed = Fav.checkDomain("http://google.com")
        assertTrue(allowed)
    }
}