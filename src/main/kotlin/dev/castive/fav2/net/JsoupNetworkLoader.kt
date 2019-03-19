package dev.castive.fav2.net

import com.django.log2.logging.Log
import org.jsoup.Jsoup

class JsoupNetworkLoader: NetworkLoader {
    override fun getIconPath(domain: String): String? {
        val document = Jsoup.connect(domain).get()
        val validIcons = arrayListOf<String>()
        val icon = document.head().select("link[rel]").select("link[href]")
        Log.d(javaClass, "Loaded ${icon.size} icons")
        icon.forEach {
            if(it.attr("rel").contains("icon")) {
                validIcons.add(it.attr("href"))
                Log.d(javaClass, it.attr("href"))
            }
        }
        return if(validIcons.isEmpty()) null else validIcons[0]
    }
}