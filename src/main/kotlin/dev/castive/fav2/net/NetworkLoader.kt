package dev.castive.fav2.net

interface NetworkLoader {
    fun getIconPath(domain: String): String?
}