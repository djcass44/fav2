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
        val document = Jsoup.connect(domain).get()
        val validIcons = arrayListOf<String>()
        val icon = document.head().select("link[rel]").select("link[href]")
        if(Fav.DEBUG) Log.d(javaClass, "Loaded ${icon.size} links")
        icon.forEach {
            if(Definitions.contains(it.attr("rel"))) {
                validIcons.add(it.attr("href"))
                if(Fav.DEBUG) Log.d(javaClass, it.attr("href"))
            }
        }
        return if(validIcons.isEmpty()) null else validIcons[0]
    }
}