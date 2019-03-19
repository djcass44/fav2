package dev.castive.fav2.net

import com.django.log2.logging.Log
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class NetworkLoaderTest {
    @ParameterizedTest
    @ValueSource(strings = [
        "https://github.com"
    ])
    fun getKnownDocument(value: String) {
        val loader = JsoupNetworkLoader()
        val icon = loader.getIconPath(value)
        Log.d(javaClass, icon)
        assertNotNull(icon)
        assertTrue(icon!!.endsWith("png") || icon.endsWith("ico"))
    }
    @ParameterizedTest
    @ValueSource(strings = [
        "https://google.com",
        "https://apple.com"
    ])
    fun getFailDocument(value: String) {
        val loader = JsoupNetworkLoader()
        val icon = loader.getIconPath(value)
        Log.d(javaClass, icon)
        assertNull(icon)
    }
    @ParameterizedTest
    @ValueSource(strings = [
        "https://github.com",
        "https://google.com",
        "https://apple.com"
    ])
    fun getKnownDirect(value: String) {
        val loader = DirectNetworkLoader()
        val icon = loader.getIconPath(value)
        Log.d(javaClass, icon)
        assertNotNull(icon)
        assertTrue(icon!!.endsWith("png") || icon.endsWith("ico"))
    }
}