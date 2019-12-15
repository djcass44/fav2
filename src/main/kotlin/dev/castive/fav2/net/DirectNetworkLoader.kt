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

import dev.castive.log2.Log
import dev.castive.log2.loge
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URI

class DirectNetworkLoader : NetworkLoader {
	private val imageMimes = arrayListOf("png", "ico")
	private val client = OkHttpClient()

	override fun getIconPath(domain: String): String = try {
		val uri = URI(domain)
		val host = "${uri.scheme}://${uri.host}"
		imageMimes.mapNotNull {
			getIcon("$host/favicon.$it")
		}.firstOrNull() ?: ""
	}
	catch (e: Exception) {
		"Failed to get icon path: $domain".loge(javaClass, e)
		""
	}
	private fun getIcon(target: String): String? {
		Log.d(javaClass, "Targeting host $target")
		val request = Request.Builder().url(target).head().build()
		return try {
			val r = client.newCall(request).execute()
			val xHeader = r.header("Content-Type")
			Log.v(javaClass, "Domain XHeader: $xHeader")
			// Check that the response has a { Content-Type: 'image/...' } header
			// This may need to be relaxed if websites don't use that mime
			if(xHeader != null && xHeader.startsWith("image")) {
				r.close()
				return target
			}
			r.close()
			null
		}
		catch (e: Exception) {
			Log.v(javaClass, "Failed to get direct favicon at location: $target", e)
			null
		}
	}
}
