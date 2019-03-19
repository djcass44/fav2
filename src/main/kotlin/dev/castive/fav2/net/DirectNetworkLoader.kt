package dev.castive.fav2.net

import com.django.log2.logging.Log
import dev.castive.fav2.Fav
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URI

class DirectNetworkLoader: NetworkLoader {
    companion object {
        val client = OkHttpClient()
    }
    override fun getIconPath(domain: String): String? {
        val uri = URI(domain)
        val host = "${uri.scheme}://${uri.host}"
        val expectedUri = "$host/favicon.ico"
        if(Fav.DEBUG) Log.d(javaClass, "Targeting host $expectedUri")
        // Check that the favicon exists
        val request = Request.Builder().url(expectedUri).head().build()
        return try {
            val r = client.newCall(request).execute()
            val xHeader = r.header("Content-Type")
            if(Fav.DEBUG) Log.v(javaClass, "Domain XHeader: $xHeader")
            // Check that the response has a { Content-Type: 'image/...' } header
            // This may need to be relaxed if websites don't use that mime
            if(xHeader != null && xHeader.startsWith("image"))
                return expectedUri
            ""
        }
        catch (e: Exception) {
            if(Fav.DEBUG) Log.v(javaClass, "Failed to get direct favicon")
            ""
        }
    }
}