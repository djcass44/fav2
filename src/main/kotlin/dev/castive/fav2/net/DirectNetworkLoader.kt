/*
 *    Copyright 2019 Django Cass
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package dev.castive.fav2.net

import com.django.log2.logging.Log
import dev.castive.fav2.Fav
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URI

class DirectNetworkLoader: NetworkLoader {
    companion object {
        val client = OkHttpClient()

        val imageMimes = arrayListOf(
            "png",
            "jpg",
            "jpeg",
            "ico"
        )
    }
    override fun getIconPath(domain: String): String {
        val uri = URI(domain)
        val host = "${uri.scheme}://${uri.host}"
        for (mime in imageMimes) {
            val res = getIcon("$host/favicon.$mime")
            if(res != null) return res
        }
        return ""
    }
    private fun getIcon(target: String): String? {
        if(Fav.DEBUG) Log.d(javaClass, "Targeting host $target")
        val request = Request.Builder().url(target).head().build()
        return try {
            val r = client.newCall(request).execute()
            val xHeader = r.header("Content-Type")
            if(Fav.DEBUG) Log.v(javaClass, "Domain XHeader: $xHeader")
            // Check that the response has a { Content-Type: 'image/...' } header
            // This may need to be relaxed if websites don't use that mime
            if(xHeader != null && xHeader.startsWith("image"))
                return target
            null
        }
        catch (e: Exception) {
            if(Fav.DEBUG) Log.v(javaClass, "Failed to get direct favicon")
            null
        }
    }
}