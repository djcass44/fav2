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

import dev.castive.fav2.entity.Icon
import dev.castive.fav2.net.DirectNetworkLoader
import dev.castive.fav2.net.JsoupNetworkLoader
import dev.castive.fav2.repo.IconRepo
import dev.castive.fav2.service.ImageUtils
import dev.castive.log2.Log
import dev.castive.log2.loge
import dev.castive.log2.logok
import dev.castive.log2.logv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.net.URI
import javax.imageio.ImageIO

@Service
class Fav(
	private val iconRepo: IconRepo,
	private val imageUtils: ImageUtils,
	private val direct: DirectNetworkLoader,
	private val jsoup: JsoupNetworkLoader
) {

	companion object {
		/**
		 * Get the destination filename
		 */
		fun dest(path: String): String = "${URI(path).host.replace(".", "_")}.png"
	}

	/**
	 * Concurrently get and download a favicon
	 * The favicon is saved to disk for later use
	 */
	private suspend fun downloadDomain(domain: String, path: String) = withContext(Dispatchers.IO) {
		val uri = kotlin.runCatching { URI(path) }.onFailure {
			"Failed to parse url: $path".loge(Fav::class.java, it)
		}.getOrNull() ?: return@withContext
		Log.i(Fav::class.java, "Starting to download image at path: $path")
		// Use ImageIO to load the image into a BufferedImage
		val image = runCatching {
			ImageIO.read(uri.toURL())
		}.onFailure {
			"Failed to load favicon data: $domain".loge(Fav::class.java, it)
		}.onSuccess {
			"Successfully downloaded image: $path".logok(Fav::class.java)
		}
		val data = image.getOrNull() ?: run {
			Log.w(Fav::class.java, "Got no image for target: $path")
			return@withContext
		}
		"Loading item $path into cache with key: '${dest(domain)}'".logv(Fav::class.java)
		iconRepo.save(Icon(dest(domain), imageUtils.biToBase64(data)))
	}

	fun loadDomain(domain: String, skipDownload: Boolean = false) {
		if(!checkDomain(domain))
			return
		var icon: String? = direct.getIconPath(domain)
		Log.i(javaClass, "Got icon address: $icon")
		if(icon != null && icon.isNotBlank()) {
			if(!skipDownload) GlobalScope.launch { downloadDomain(domain, icon!!) }
			return
		}
		Log.i(javaClass, "Icon is unacceptable, using fallback manual check")
		icon = jsoup.getIconPath(domain)
		if(icon != null && icon.isNotBlank() && !skipDownload) {
			GlobalScope.launch {
				downloadDomain(domain, icon)
			}
		}
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
