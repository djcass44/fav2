package dev.castive.fav2.http.api

import dev.castive.fav2.Fav
import dev.castive.fav2.util.EnvUtil
import dev.castive.log2.Log
import io.javalin.apibuilder.ApiBuilder
import io.javalin.apibuilder.EndpointGroup
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import io.javalin.http.NotFoundResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.eclipse.jetty.http.HttpStatus
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.util.concurrent.CompletableFuture

class Icons(private val fav: Fav = Fav()): EndpointGroup {
	private val dataPath = EnvUtil.getEnv(EnvUtil.FAV_DATA, "/data")

	override fun addEndpoints() {
		ApiBuilder.post("/icon") { ctx ->
			val domain = getSiteParam(ctx)
			val future = CompletableFuture<String?>()
			GlobalScope.launch { fav.loadDomain(domain, future) }
			ctx.status(HttpStatus.OK_200).result(future)
		}
		ApiBuilder.get("/icon") { ctx ->
			val domain = getSiteParam(ctx)
			val targetFile = File("$dataPath${File.separator}${URI(domain).host.replace(".", "_")}.png")
			Log.i(javaClass, "Serving file: ${targetFile.absolutePath}")
			if(!targetFile.exists()) run {
				// The user has requested a url which we haven't downloaded yet, so download it for next time
				GlobalScope.launch { fav.loadDomain(domain) }
				throw NotFoundResponse("That icon hasn't been downloaded yet")
			}
			val data = FileInputStream(targetFile)
			ctx.status(HttpStatus.OK_200).contentType("image/png").result(data)
		}
	}

	/**
	 * Get the ?site parameter from the context
	 */
	private fun getSiteParam(ctx: Context): String {
		val domain = ctx.queryParam("site", String::class.java, "").getOrNull()
		if(domain.isNullOrBlank()) throw BadRequestResponse("?site may not be null, empty or only whitespace")
		return domain
	}
}