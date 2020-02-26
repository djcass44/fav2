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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URI

@Service
class DirectNetworkLoader @Autowired constructor(
	private val restTemplate: RestTemplate
): NetworkLoader {
	private val imageMimes = listOf("png", "ico")

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
		"Targeting host $target".logd(javaClass)
		return try {
			val response = restTemplate.exchange(target, HttpMethod.HEAD, HttpEntity.EMPTY, Nothing::class.java)
			val contentType = response.headers.contentType
			"Domain contentType: $contentType".logd(javaClass)
			// Check that the response has a { Content-Type: 'image/...' } header
			// This may need to be relaxed if websites don't use that mime (case and point - DockerHub uses octet stream)
			if(contentType != null && (contentType.isCompatibleWith(MediaType.parseMediaType("image/*")) || contentType == MediaType.APPLICATION_OCTET_STREAM)) {
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
