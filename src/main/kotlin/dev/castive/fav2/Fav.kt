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

package dev.castive.fav2

import dev.castive.fav2.net.DirectNetworkLoader
import dev.castive.fav2.net.JsoupNetworkLoader
import dev.castive.fav2.service.ImageUtils
import dev.castive.log2.Log
import dev.castive.log2.loge
import dev.castive.log2.logi
import dev.castive.log2.logok
import dev.castive.log2.logw
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.net.URI
import javax.imageio.ImageIO

@Service
class Fav(
	private val imageUtils: ImageUtils,
	private val direct: DirectNetworkLoader,
	private val jsoup: JsoupNetworkLoader
) {

	/**
	 * Concurrently get and download a favicon
	 * The favicon is saved to disk for later use
	 */
	private suspend fun downloadDomain(domain: String, path: String): BufferedImage? {
		val uri = kotlin.runCatching {
			URI(path)
		}.onFailure {
			"Failed to parse url: $path".loge(Fav::class.java, it)
		}.getOrNull() ?: return null


		"Starting to download image at path: $path".logi(Fav::class.java)
		// Use ImageIO to load the image into a BufferedImage
		val image = runCatching {
			ImageIO.read(uri.toURL())
		}.onFailure {
			"Failed to load favicon data: $domain".loge(Fav::class.java, it)
		}.onSuccess {
			"Successfully downloaded image: $path".logok(Fav::class.java)
		}
		return image.getOrNull() ?: run {
			"Got no image for target: $path".logw(Fav::class.java)
			return null
		}
	}

	suspend fun loadDomain(domain: String, skipDownload: Boolean = false): BufferedImage? {
		if(!checkDomain(domain)) {
			"Domain failed minimum checks: $domain".loge(javaClass)
			return null
		}
		var icon: String? = direct.getIconPath(domain)
		Log.i(javaClass, "Got icon address: $icon")
		if(icon != null && icon.isNotBlank()) {
			if(!skipDownload)
				return downloadDomain(domain, icon)
			return null
		}
		"Icon is unacceptable, using fallback manual check".logi(javaClass)
		icon = jsoup.getIconPath(domain)
		if(icon != null && icon.isNotBlank() && !skipDownload) {
			return downloadDomain(domain, icon)
		}
		return null
	}

	/**
	 * Check whether we are allowed to probe a domain
	 * Returns false if protocol is HTTP (without TLS)
	 */
	internal fun checkDomain(domain: String): Boolean {
		if(domain.startsWith("http://")) Log.w(javaClass, "Loading of insecure origins is not recommended.")
		return !domain.startsWith("http://")
	}
}
