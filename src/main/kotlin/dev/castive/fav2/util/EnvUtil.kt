package dev.castive.fav2.util

internal object EnvUtil {
    fun getEnv(name: String, default: String = ""): String {
        val env = System.getenv(name)
        return if (env.isNullOrEmpty()) default else env
    }

    const val FAV_ALLOW_CORS = "FAV_ALLOW_CORS"
    const val FAV_DEBUG = "FAV_DEBUG"
    const val FAV_HTTP_PORT = "FAV_HTTP_PORT"
	const val FAV_DATA = "FAV_DATA"
    const val FAV_BASE_URL = "FAV_BASE_URL"
}