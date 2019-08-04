package dev.castive.fav2.http.api

import io.javalin.http.Handler
import org.eclipse.jetty.http.HttpStatus

object Health {
	val get: Handler = Handler { ctx ->
		// return 200. If we can't do that then the http server is unhealthy
		ctx.status(HttpStatus.OK_200).result("OK")
	}
}