/*
 *  Copyright 2019 Django Cass
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
 */

package dev.castive.fav2.http

import dev.castive.fav2.Fav
import dev.castive.fav2.TimedCache
import dev.castive.fav2.http.api.Health
import dev.castive.fav2.http.api.Icons
import dev.castive.fav2.util.EnvUtil
import dev.castive.fav2.util.env
import dev.castive.log2.Log
import io.javalin.Javalin
import io.javalin.http.HandlerType
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.openapi.ui.SwaggerOptions
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class App {
	/**
	 * Starts the HTTP server
	 */
    suspend fun start(): Unit = withContext(Dispatchers.Default) {
		val icons = Icons(cache = TimedCache(
			listener = Fav.cacheListener,
			ageLimit = EnvUtil.FAV_CACHE_LIMIT.env("30").toIntOrNull() ?: 30,
			tickDelay = EnvUtil.FAV_CACHE_TICK.env("10000").toLongOrNull() ?: 10_000L
		))
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
	            registerPlugin(OpenApiPlugin(getOpenApiOptions()))
            }
        }
	        // returns 200 OK
	        .addHandler(HandlerType.GET, "/healthz", Health()::get)
	        .addHandler(HandlerType.GET, "/icon", icons::getIcon)
	        .addHandler(HandlerType.POST, "/icon", icons::postIcon)
	        // Start on 8080 unless overridden by environment variable
	        .start(EnvUtil.getEnv(EnvUtil.FAV_HTTP_PORT, "8080").toIntOrNull() ?: 8080)
	    return@withContext
    }

	private fun getOpenApiOptions() = OpenApiOptions(Info().apply {
		license = License().name("Apache License 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html")
		title = "Fav2"
		version = "0.3"
		description = "Microservice for loading favicons"
	}).path("/swagger-docs").swagger(SwaggerOptions("/"))
}