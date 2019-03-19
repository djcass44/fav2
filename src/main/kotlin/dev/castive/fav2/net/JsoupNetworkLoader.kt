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

import com.django.log2.logging.Log
import dev.castive.fav2.Definitions
import dev.castive.fav2.Fav
import org.jsoup.Jsoup

class JsoupNetworkLoader: NetworkLoader {
    /**
     * Attempt to load the sites favicon by searching the links within the <head></head>
     * E.g. <link rel="shortcut icon" href="https://github.githubassets.com/favicon.ico">
     */
    override fun getIconPath(domain: String): String? {
        try {
            val document = Jsoup.connect(domain).get()
            val validIcons = arrayListOf<String>()
            val icon = document.head().select("link[rel]").select("link[href]")
            if (Fav.DEBUG) Log.d(javaClass, "Loaded ${icon.size} links")
            icon.forEach {
                if (Definitions.contains(it.attr("rel"))) {
                    validIcons.add(it.attr("href"))
                    if (Fav.DEBUG) Log.d(javaClass, it.attr("href"))
                }
            }
            return if (validIcons.isEmpty()) null else validIcons[0]
        }
        catch (e: Exception) {
            Log.e(javaClass, "Failed to get icon: $e")
            return null
        }
    }
}