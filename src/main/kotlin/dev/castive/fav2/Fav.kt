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
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

class Fav(
	private val debug: Boolean = EnvUtil.getEnv(EnvUtil.FAV_DEBUG, "false").toBoolean(),
	private val allowHttp: Boolean = EnvUtil.getEnv(EnvUtil.FAV_ALLOW_HTTP, "false").toBoolean()
) {
	private val dataPath = EnvUtil.getEnv(EnvUtil.FAV_DATA, "/data")

	private suspend fun downloadDomain(path: String) = withContext(Dispatchers.IO) {
		val uri = URI(path)
		Log.i(javaClass, "Starting to download image at path: ${uri.toURL()}")
		val image = runCatching { ImageIO.read(uri.toURL()) }
		val err = image.exceptionOrNull()
		if (err != null) Log.e(javaClass, "Failed to load favicon data: $err")
		val data = image.getOrNull() ?: run {
			Log.w(javaClass, "Got no image for target: $path")
			return@withContext
		}
		val name = uri.host.replace(".", "_")
		val extension = uri.path.split(".").last()
		Log.i(javaClass, "Got extension: $extension")
		try {
			val file = File("$dataPath${File.separator}$name.png")
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
	fun loadDomain(domain: String): String? {
		if(!checkDomain(domain)) return null
		var icon: String? = DirectNetworkLoader(debug).getIconPath(domain)
		if(icon != null && icon.isNotBlank()) {
			GlobalScope.launch { downloadDomain(icon!!) }
			return icon
		}

		icon = JsoupNetworkLoader(debug).getIconPath(domain)
		if(icon != null && icon.isNotBlank()) {
			GlobalScope.launch { downloadDomain(icon) }
			return icon
		}

		return null
	}
	fun loadDomain(domain: String, callback: OnLoadedCallback) {
		if(!checkDomain(domain)) {
			callback.onLoad(null)
			return
		}
		GlobalScope.launch {
			var icon: String? = DirectNetworkLoader(debug).getIconPath(domain)
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
		if(domain.startsWith("http://") && allowHttp && debug) Log.w(javaClass, "Loading of insecure origins is not recommended.")
		return !domain.startsWith("http://") || allowHttp
	}

	interface OnLoadedCallback {
		fun onLoad(favicon: String?)
	}
}