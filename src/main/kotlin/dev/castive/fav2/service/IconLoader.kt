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
import dev.castive.fav2.TimedCache
import dev.castive.fav2.config.AppConfig
import dev.castive.fav2.config.CacheConfig
import dev.castive.fav2.error.BadRequestResponse
import dev.castive.log2.loge
import dev.castive.log2.logi
import dev.castive.log2.logv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.*
import javax.imageio.ImageIO

@Service
class IconLoader @Autowired constructor(
	private val config: AppConfig,
	cacheConfig: CacheConfig
) {
	private val cache = TimedCache(
		cacheConfig.limit,
		cacheConfig.delay,
		Fav.cacheListener
	)
	private val fav = Fav(cache = cache, baseUrl = config.url)

	private val prefixInsecure = "http://"
	private val prefixSecure = "https://"


	fun loadStream(url: String): InputStream? {
		val domain = getBestUrl(url)
		val name = Fav.dest(domain)
		val targetFile = try {
			File("${config.path}${File.separator}$name")
		}
		catch (e: Exception) {
			"Unable to parse domain: $domain".loge(javaClass)
			return null
		}
		val existing = cache[name]
		return if(existing != null) {
			"Located cached item for $name".logi(javaClass)
			// convert the BufferedImage into an inputstream
			val output = ByteArrayOutputStream()
			ImageIO.write(existing, "png", output)
			ByteArrayInputStream(output.toByteArray())
		}
		else {
			"Attempting to serve file: ${targetFile.absolutePath}".logi(javaClass)
			if(!targetFile.exists()) run {
				// The user has requested a url which we haven't downloaded yet, so download it for next time
				GlobalScope.launch {
					fav.loadDomain(domain)
				}
				return null
			}
			// load the file into the cache
			GlobalScope.launch {
				withContext(Dispatchers.IO) {
					"Loading item $name into cache from disk".logv(javaClass)
					cache[name] = ImageIO.read(targetFile)
				}
			}
			FileInputStream(targetFile)
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