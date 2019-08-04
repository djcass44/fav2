package dev.castive.fav2.http.api

import io.javalin.http.Handler
import org.eclipse.jetty.http.HttpStatus

object Health {
	val get: Handler = Handler { ctx ->
		ctx.status(HttpStatus.OK_200).result("OK")
	}
}