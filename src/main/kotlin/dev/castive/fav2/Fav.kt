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

import dev.castive.fav2.config.AppConfig
import dev.castive.fav2.net.DirectNetworkLoader
import dev.castive.fav2.net.JsoupNetworkLoader
import dev.castive.fav2.util.safe
import dev.castive.log2.Log
import dev.castive.log2.loge
import dev.castive.log2.logok
import dev.castive.log2.logv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.image.BufferedImage
import java.net.URI
import javax.imageio.ImageIO

class Fav(
	private val cache: TimedCache<String, BufferedImage>,
	private val appConfig: AppConfig
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
			"Failed to parse url: $path".loge(Fav::class.java)
		}.getOrNull() ?: return@withContext
		Log.i(Fav::class.java, "Starting to download image at path: $path")
		// Use ImageIO to load the image into a BufferedImage
		val image = runCatching { ImageIO.read(uri.toURL()) }.onFailure {
			"Failed to load favicon data: $it".loge(Fav::class.java)
		}.onSuccess {
			"Successfully downloaded image: $path".logok(Fav::class.java)
		}
		val data = image.getOrNull() ?: run {
			Log.w(Fav::class.java, "Got no image for target: $path")
			return@withContext
		}
		"Loading item $path into cache with key: '${dest(domain)}'".logv(Fav::class.java)
		cache[dest(domain)] = data
	}

	fun loadDomain(domain: String, skipDownload: Boolean = false): String? {
		if(!checkDomain(domain)) return null
		var icon: String? = DirectNetworkLoader().getIconPath(domain)
		Log.i(javaClass, "Got icon address: $icon")
		if(icon != null && icon.isNotBlank()) {
			if(!skipDownload) GlobalScope.launch { downloadDomain(domain, icon!!) }
			return "${appConfig.url}/icon?site=${domain.safe()}"
		}
		Log.i(javaClass, "Icon is unacceptable, using fallback manual check")
		icon = JsoupNetworkLoader().getIconPath(domain)
		if(icon != null && icon.isNotBlank()) {
			if(!skipDownload) GlobalScope.launch { downloadDomain(domain, icon) }
			return "${appConfig.url}/icon?site=${domain.safe()}"
		}

		return null
	}

	internal fun checkDomain(domain: String): Boolean {
		if(domain.startsWith("http://")) Log.w(javaClass, "Loading of insecure origins is not recommended.")
		return !domain.startsWith("http://")
	}
}
