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
import dev.castive.fav2.util.EnvUtil
import dev.castive.log2.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

class Fav(
	private val debug: Boolean = EnvUtil.getEnv(EnvUtil.FAV_DEBUG, "false").toBoolean()
) {
	private val dataPath = EnvUtil.getEnv(EnvUtil.FAV_DATA, "/data")
	private val baseUrl = EnvUtil.getEnv(EnvUtil.FAV_BASE_URL, "http://localhost:8080")

	/**
	 * Get the destination filename
	 */
	private fun dest(path: String): String = "${URI(path).host.replace(".", "_")}.png"

	/**
	 * Convert a string to be url safe
	 */
	private fun safe(string: String): String = URLEncoder.encode(string, StandardCharsets.UTF_8)

	/**
	 * Concurrently get and download a favicon
	 * The favicon is saved to disk for later use
	 */
	private suspend fun downloadDomain(path: String) = withContext(Dispatchers.IO) {
		val uri = URI(path)
		Log.i(javaClass, "Starting to download image at path: ${uri.toURL()}")
		// Use ImageIO to load the image into a BufferedImage
		val image = runCatching { ImageIO.read(uri.toURL()) }
		val err = image.exceptionOrNull()
		// Handle if the returned image is null
		if (err != null) Log.e(javaClass, "Failed to load favicon data: $err")
		val data = image.getOrNull() ?: run {
			Log.w(javaClass, "Got no image for target: $path")
			return@withContext
		}
		try {
			// Write the BufferedImage to disk as a png
			val file = File("$dataPath${File.separator}${dest(path)}")
			ImageIO.write(data, "png", file)
			Log.i(javaClass, "Wrote data to path: ${file.absolutePath}")
		}
		catch (e: IOException) {
			Log.e(javaClass, "Failed to write data: $e")
		}
	}

	fun loadDomain(domain: String, future: CompletableFuture<String?>) {
		future.complete(loadDomain(domain))
	}
	fun loadDomain(domain: String, skipDownload: Boolean = false): String? {
		if(!checkDomain(domain)) return null
		var icon: String? = DirectNetworkLoader().getIconPath(domain)
		Log.i(javaClass, "Got icon address: $icon")
		if(icon != null && icon.isNotBlank()) {
			if(!skipDownload) GlobalScope.launch { downloadDomain(icon!!) }
			return "$baseUrl/icon?site=${safe(domain)}"
		}
		Log.i(javaClass, "Icon is unacceptable, using fallback manual check")
		icon = JsoupNetworkLoader(debug).getIconPath(domain)
		if(icon != null && icon.isNotBlank()) {
			if(!skipDownload) GlobalScope.launch { downloadDomain(icon) }
			return "$baseUrl/icon?site=${safe(domain)}"
		}

		return null
	}
	fun loadDomain(domain: String, callback: OnLoadedCallback) {
		if(!checkDomain(domain)) {
			callback.onLoad(null)
			return
		}
		GlobalScope.launch {
			var icon: String? = DirectNetworkLoader().getIconPath(domain)
			if(icon != null && icon.isNotBlank()) {
				callback.onLoad(icon)
				return@launch
			}
			// if we found nothing, fallback to the slower DOM analysis
			icon = JsoupNetworkLoader(debug).getIconPath(domain)
			if(icon != null && icon.isNotBlank()) {
				callback.onLoad(icon)
				return@launch
			}
			callback.onLoad(null)
		}
	}

	internal fun checkDomain(domain: String): Boolean {
		if(domain.startsWith("http://") && debug) Log.w(javaClass, "Loading of insecure origins is not recommended.")
		return !domain.startsWith("http://")
	}

	interface OnLoadedCallback {
		fun onLoad(favicon: String?)
	}
}