package dev.castive.fav2.net

import com.django.log2.logging.Log
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class NetworkLoaderTest {
    @ParameterizedTest
    @ValueSource(strings = [
        "https://github.com",
        "https://google.com",
        "https://apple.com"
    ])
    fun getKnownPositive(value: String) {
        val loader = JsoupNetworkLoader()
        val icon = loader.getIconPath(value)
        Log.d(javaClass, icon)
        assertNotNull(icon)
        assertTrue(icon!!.endsWith("png") || icon.endsWith("ico"))
    }
}