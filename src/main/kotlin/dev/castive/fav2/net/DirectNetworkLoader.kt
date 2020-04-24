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

import dev.castive.log2.logd
import dev.castive.log2.loge
import dev.castive.log2.logv
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitExchange
import java.net.URI

@Service
class DirectNetworkLoader(
	private val webClient: WebClient
): NetworkLoader {
	private val imageMimes = listOf("png", "ico")

	override suspend fun getIconPath(domain: String): String? = try {
		val uri = URI(domain)
		val host = "${uri.scheme}://${uri.host}"
		imageMimes.mapNotNull {
			getIcon("$host/favicon.$it")
		}.firstOrNull() ?: ""
	}
	catch (e: Exception) {
		"Failed to get icon path: $domain".loge(javaClass, e)
		null
	}
	private suspend fun getIcon(target: String): String? {
		"Targeting host $target".logd(javaClass)
		return try {
			val response = webClient.head()
				.uri(target)
				.awaitExchange()
			val contentTypeHolder = response.headers().contentType()
			if(contentTypeHolder.isEmpty) {
				"Got null contentType for target: $target".loge(javaClass)
				return null
			}
			val contentType = contentTypeHolder.get()
			"Domain contentType: $contentType".logd(javaClass)
			// Check that the response has a { Content-Type: 'image/...' } header
			// This may need to be relaxed if websites don't use that mime (case and point - DockerHub uses octet stream)
			if((contentType.isCompatibleWith(MediaType.parseMediaType("image/*")) || contentType == MediaType.APPLICATION_OCTET_STREAM)) {
				return target
			}
			null
		}
		catch (e: Exception) {
			"Failed to get direct favicon at location: $target".logv(javaClass, e)
			null
		}
	}
}
