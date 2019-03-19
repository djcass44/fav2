package dev.castive.fav2

import com.django.log2.logging.Log
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class FavTest {
    @BeforeEach
    internal fun setUp() {
        Fav.DEBUG = true
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
        Assertions.assertNotNull(icon)
        Assertions.assertTrue(icon!!.endsWith("png") || icon.endsWith("ico"))
    }
}