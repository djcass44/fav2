package dev.castive.fav2

import dev.castive.fav2.net.DirectNetworkLoader
import dev.castive.fav2.net.JsoupNetworkLoader

class Fav {
    companion object {
        private lateinit var single: Fav

        fun getInstance(): Fav {
            if(!this::single.isInitialized) single = Fav()
            return single
        }

        var DEBUG = false
    }
    fun loadDomain(domain: String): String? {
        var icon: String? = DirectNetworkLoader().getIconPath(domain)
        if(icon != null && icon.isNotBlank()) return icon

        icon = JsoupNetworkLoader().getIconPath(domain)
        if(icon != null && icon.isNotBlank()) return icon

        return null
    }
    fun loadDomain(domain: String, callback: OnLoadedCallback) {
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

    interface OnLoadedCallback {
        fun onLoad(favicon: String?)
    }
}