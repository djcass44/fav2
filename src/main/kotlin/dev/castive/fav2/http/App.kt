package dev.castive.fav2.http

import dev.castive.fav2.http.api.Health
import dev.castive.fav2.util.EnvUtil
import io.javalin.Javalin
import io.javalin.http.HandlerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class App {
    suspend fun start(): Unit = withContext(Dispatchers.Default) {
        Javalin.create { config ->
            config.apply {
                showJavalinBanner = false
                if(EnvUtil.getEnv(EnvUtil.FAV_ALLOW_CORS, "false").toBoolean()) enableCorsForAllOrigins()
            }
        }
	        .addHandler(HandlerType.GET, "/healthz", Health.get)
	        .routes {}
	        .start(EnvUtil.getEnv(EnvUtil.FAV_HTTP_PORT, "8080").toIntOrNull() ?: 8080)
	    return@withContext
    }
}