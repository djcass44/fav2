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

import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiResponse
import org.eclipse.jetty.http.HttpStatus

class Health {
	@OpenApi(
		summary = "Get health",
		description = "Check the api health",
		tags = ["system"],
		responses = [
			OpenApiResponse("200", [OpenApiContent()], description = "Everything is okay"),
			OpenApiResponse("500", [OpenApiContent()], description = "Something is wrong")
		]
	)
	fun get(ctx: Context) {
		// return 200. If we can't do that then the http server is unhealthy
		ctx.status(HttpStatus.OK_200).result("OK")
	}
}