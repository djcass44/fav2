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
import dev.dcas.util.spring.responses.BadRequestResponse
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage

@Service
class IconLoader(
	private val fav: Fav
) {

	private val prefixInsecure = "http://"
	private val prefixSecure = "https://"


	@Cacheable(value = ["icon"], key = "#a0")
	suspend fun getIconFromUrl(hash: Int, url: String): BufferedImage? {
		val domain = getBestUrl(url)
		// The user has requested a url which we haven't downloaded yet, so download it
		return fav.loadDomain(domain)
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
