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

import com.google.common.util.concurrent.RateLimiter
import dev.castive.fav2.Fav
import dev.castive.fav2.props.AppConfig
import dev.castive.fav2.repo.IconRepo
import dev.castive.log2.loge
import dev.castive.log2.logi
import dev.castive.log2.logv
import dev.dcas.util.spring.responses.BadRequestResponse
import dev.dcas.util.spring.responses.RateLimitResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*

@Service
class IconLoader(
	private val imageUtils: ImageUtils,
	private val config: AppConfig,
	private val fav: Fav,
	private val iconRepo: IconRepo
) {

	private val prefixInsecure = "http://"
	private val prefixSecure = "https://"

	private val limit = RateLimiter.create(config.rate)


	fun deleteFromCache(url: String): Boolean {
		val domain = getBestUrl(url)
		val name = Fav.dest(domain)

		return kotlin.runCatching {
			iconRepo.deleteById(name)
			return true
		}.onFailure {
			"Failed to remove item from cache: $name".loge(javaClass, it)
		}.getOrNull() ?: false
	}

	fun peekCache(): List<Pair<String, Int>> = iconRepo.findAll().map {
		it.name to it.age
	}


	fun loadStream(url: String): InputStream? {
		// check if we are being throttled
		val permit = limit.tryAcquire()
		if(!permit) {
			"Rate limiting request for $url".logv(javaClass)
			throw RateLimitResponse()
		}
		val domain = getBestUrl(url)
		val name = Fav.dest(domain)

		val existing = iconRepo.findByIdOrNull(name)
		return if(existing != null) {
			"Located cached item for $name".logi(javaClass)
			// convert the data into an inputstream
			ByteArrayInputStream(
				Base64.getDecoder().decode(existing.imageData)
			)
		}
		else {
			// The user has requested a url which we haven't downloaded yet, so download it for next time
			"Failed to locate cached file for url: $name".logi(javaClass)
			GlobalScope.launch {
				fav.loadDomain(domain)
			}
			return null
		}
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
