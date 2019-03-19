package dev.castive.fav2

import com.django.log2.logging.Log
import dev.castive.fav2.net.DirectNetworkLoader
import dev.castive.fav2.net.JsoupNetworkLoader

object Fav {
    var DEBUG = false
    var ALLOW_HTTP = false

    fun loadDomain(domain: String): String? {
        if(!checkDomain(domain)) return null
        var icon: String? = DirectNetworkLoader().getIconPath(domain)
        if(icon != null && icon.isNotBlank()) return icon

        icon = JsoupNetworkLoader().getIconPath(domain)
        if(icon != null && icon.isNotBlank()) return icon

        return null
    }
    fun loadDomain(domain: String, callback: OnLoadedCallback) {
        if(!checkDomain(domain)) {
            callback.onLoad(null)
            return
        }
        Thread {
            var icon: String? = DirectNetworkLoader().getIconPath(domain)
            if(icon != null && icon.isNotBlank()) {
                callback.onLoad(icon)
                return@Thread
            }

            icon = JsoupNetworkLoader().getIconPath(domain)
            if(icon != null && icon.isNotBlank()) {
                callback.onLoad(icon)
                return@Thread
            }

            callback.onLoad(null)
        }.apply {
            isDaemon = true
            start()
        }
    }

    internal fun checkDomain(domain: String): Boolean {
        if(domain.startsWith("http://") && ALLOW_HTTP && DEBUG) Log.w(javaClass, "Loading of insecure origins is not recommended.")
        return !domain.startsWith("http://") || ALLOW_HTTP
    }

    interface OnLoadedCallback {
        fun onLoad(favicon: String?)
    }
}