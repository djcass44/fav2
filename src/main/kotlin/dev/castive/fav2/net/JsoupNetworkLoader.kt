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

import dev.castive.fav2.Definitions
import dev.castive.log2.Log
import dev.castive.log2.logd
import dev.castive.log2.loge
import dev.castive.log2.logv
import org.jsoup.Jsoup
import org.springframework.stereotype.Service

@Service
class JsoupNetworkLoader: NetworkLoader {
	/**
	 * Attempt to load the sites favicon by searching the links within the <head></head>
	 * E.g. <link rel="shortcut icon" href="https://github.githubassets.com/favicon.ico">
	 */
	override suspend fun getIconPath(domain: String): String? {
		try {
			val document = Jsoup.connect(domain).get()
			val validIcons = arrayListOf<String>()
			val icon = document.head().select("link[rel]").select("link[href]")
			Log.d(javaClass, "Loaded ${icon.size} links")
			icon.forEach {
				"Checking 'rel': ${it.attr("rel")}".logd(javaClass)
				if (Definitions.contains(it.attr("rel"))) {
					validIcons.add(it.attr("href"))
					"Found possible image: ${it.attr("href")}".logv(javaClass)
				}
			}
			return if (validIcons.isEmpty()) null else getAbsoluteUrl(domain, validIcons[0])
		}
		catch (e: Exception) {
			"Failed to get icon for domain: $domain".loge(javaClass, e)
			return null
		}
	}

	/**
	 * Make sure that the image url is absolute
	 * E.g. /img/favicon.png -> example.com/img/favicon.png
	 */
	internal fun getAbsoluteUrl(domain: String, imageUrl: String): String {
		val safeDomain = domain.removeSuffix("/")
		return when {
			imageUrl.startsWith("//") -> "https:${imageUrl}"
			imageUrl.startsWith("/") -> safeDomain + imageUrl
			!imageUrl.startsWith("http") -> "$safeDomain/$imageUrl"
			else -> imageUrl
		}
	}
}
