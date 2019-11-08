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

package dev.castive.fav2.http.api

import com.google.common.util.concurrent.RateLimiter
import dev.castive.fav2.Fav
import dev.castive.fav2.TimedCache
import dev.castive.fav2.util.EnvUtil
import dev.castive.log2.Log
import dev.castive.log2.logi
import dev.castive.log2.logv
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import io.javalin.http.NotFoundResponse
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiParam
import io.javalin.plugin.openapi.annotations.OpenApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eclipse.jetty.http.HttpStatus
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

class Icons(
	private val cache: TimedCache<String, BufferedImage>,
	private val fav: Fav = Fav(cache = cache)
) {
	companion object {
		const val prefixSecure = "https://"
		const val prefixInsecure = "http://"
	}

	private val dataPath = EnvUtil.getEnv(EnvUtil.FAV_DATA, "/data")

	private val limit = RateLimiter.create(5.0)

	@OpenApi(
		summary = "Post icon",
		description = "Tell the api to load a favicon",
		tags = ["icon"],
		responses = [
			OpenApiResponse("200", [OpenApiContent()], description = "Api has accepted the job"),
			OpenApiResponse("500", [OpenApiContent()], description = "Something went wrong")
		],
		queryParams = [
			OpenApiParam("site", required = true, allowEmptyValue = false)
		]
	)
	fun postIcon(ctx: Context) {
		val timeForPermit = limit.acquire()
		Log.v(javaClass, "Got POST request permit in $timeForPermit")
		val domain = getSiteParam(ctx)
		val future = CompletableFuture<String?>()
		GlobalScope.launch {
			fav.loadDomain(domain, future)
		}
		ctx.status(HttpStatus.OK_200).result(future)
	}
	@OpenApi(
		summary = "Get icon",
		description = "Serves an icon as an image",
		tags = ["icon"],
		responses = [
			OpenApiResponse("200", [OpenApiContent()], description = "Image loaded"),
			OpenApiResponse("400", [OpenApiContent()], description = "Bad request"),
			OpenApiResponse("404", [OpenApiContent()], description = "Image hasn't been downloaded yet"),
			OpenApiResponse("500", [OpenApiContent()], description = "Something went wrong")
		],
		queryParams = [
			OpenApiParam("site", required = true, allowEmptyValue = false)
		]
	)
	fun getIcon(ctx: Context) {
		val timeForPermit = limit.acquire()
		Log.v(javaClass, "Got GET request permit in $timeForPermit")
		val domain = getBestUrl(getSiteParam(ctx))
		Log.i(javaClass, "Got request for domain: $domain")
		if(!domain.startsWith(prefixSecure)) throw BadRequestResponse("Only HTTPS domains will be accepted.")
		val name = Fav.dest(domain)
		val targetFile = try {
			File("$dataPath${File.separator}$name")
		}
		catch (e: Exception) {
			e.printStackTrace()
			throw BadRequestResponse("Invalid target url: $domain")
		}
		val existing = cache[name]
		val data = if(existing != null) {
			"Located cached item for $name".logi(javaClass)
			// convert the BufferedImage into an inputstream
			val output = ByteArrayOutputStream()
			ImageIO.write(existing, "png", output)
			ByteArrayInputStream(output.toByteArray())
		}
		else {
			Log.i(javaClass, "Serving file: ${targetFile.absolutePath}")
			if(!targetFile.exists()) run {
				// The user has requested a url which we haven't downloaded yet, so download it for next time
				GlobalScope.launch { fav.loadDomain(domain) }
				throw NotFoundResponse("That icon hasn't been downloaded yet")
			}
			// load the file into the cache
			GlobalScope.launch {
				withContext(Dispatchers.IO) {
					"Loading item $name into cache from disk".logv(javaClass)
					cache[name] = ImageIO.read(targetFile)
				}
			}
			FileInputStream(targetFile)
		}
		ctx.status(HttpStatus.OK_200).contentType("image/png").result(data)
	}

	/**
	 * Get the ?site parameter from the context
	 */
	private fun getSiteParam(ctx: Context): String {
		val domain = ctx.queryParam("site", String::class.java, "").getOrNull()
		if(domain.isNullOrBlank()) throw BadRequestResponse("'site' query may be null, empty or only whitespace")
		return domain
	}

	internal fun getBestUrl(url: String): String {
		if(url.startsWith(prefixInsecure)) throw BadRequestResponse("Insecure domains will not be accepted.")
		val builder = StringBuilder()
		if(!url.startsWith(prefixSecure)) builder.append(prefixSecure).append(url)
		else builder.append(url)
		if(!url.endsWith("/")) builder.append("/")
		return builder.toString()
	}
}
