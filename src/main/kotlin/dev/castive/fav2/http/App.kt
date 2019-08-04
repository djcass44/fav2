package dev.castive.fav2.http

import dev.castive.fav2.http.api.Health
import dev.castive.fav2.http.api.Icons
import dev.castive.fav2.util.EnvUtil
import dev.castive.log2.Log
import io.javalin.Javalin
import io.javalin.http.HandlerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class App {
	/**
	 * Starts the HTTP server
	 */
    suspend fun start(): Unit = withContext(Dispatchers.Default) {
        Javalin.create { config ->
	        // custom Javalin configuration
            config.apply {
                showJavalinBanner = false
	            // allow cors requests ONLY when explicitly enabled
                if(EnvUtil.getEnv(EnvUtil.FAV_ALLOW_CORS, "false").toBoolean()) enableCorsForAllOrigins()
	            requestLogger { ctx, timeMs ->
		            // log requests to the console
		            Log.i(javaClass, "${System.currentTimeMillis()} - ${ctx.method()} ${ctx.path()} took $timeMs ms")
	            }
            }
        }
	        // returns 200 OK
	        .addHandler(HandlerType.GET, "/healthz", Health.get)
	        .routes { Icons().addEndpoints() }
	        // Start on 8080 unless overridden by environment variable
	        .start(EnvUtil.getEnv(EnvUtil.FAV_HTTP_PORT, "8080").toIntOrNull() ?: 8080)
	    return@withContext
    }
}