/*
 *  Copyright 2019 Django Cass
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
 */

package dev.castive.fav2.service

import dev.castive.fav2.Fav
import dev.castive.log2.logi
import dev.dcas.util.spring.responses.BadRequestResponse
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.util.concurrent.ConcurrentHashMap

@Service
class IconLoader(
	private val fav: Fav
) {
	private val cache: ConcurrentHashMap<Int, BufferedImage?> = ConcurrentHashMap()

	private val prefixInsecure = "http://"
	private val prefixSecure = "https://"


	suspend fun getIconFromUrl(url: String): BufferedImage? {
		cache[url.hashCode()]?.let {
			"Got cache hit for url: '$url'".logi(javaClass)
			return it
		}

		val domain = getBestUrl(url)
		// The user has requested a url which we haven't downloaded yet, so download it
		val image = fav.loadDomain(domain)
		cache[url.hashCode()] = image
		return image
	}

	internal fun getBestUrl(url: String): String {
		if(url.startsWith(prefixInsecure)) throw BadRequestResponse("Insecure domains will not be accepted.")
		val builder = StringBuilder()
		if(!url.startsWith(prefixSecure)) builder.append(prefixSecure).append(url)
		else builder.append(url)
		if(!url.endsWith("/")) builder.append("/")
		return builder.toString()
	}
}
