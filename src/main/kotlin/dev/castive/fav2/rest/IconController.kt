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

package dev.castive.fav2.rest

import dev.castive.fav2.error.BadRequestResponse
import dev.castive.fav2.service.IconLoader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/icon")
@RestController
class IconController @Autowired constructor(
	private val loader: IconLoader
) {
	@GetMapping
	fun getImage(@RequestParam site: String): ResponseEntity<*> {
		if(site.isBlank())
			throw BadRequestResponse("'site' parameter must not be blank")
		// get the stream or throw 400
		val stream = loader.loadStream(site) ?: throw BadRequestResponse()
		val headers = HttpHeaders().apply {
			contentType = MediaType.IMAGE_PNG
		}
		return ResponseEntity(InputStreamResource(stream), headers, HttpStatus.OK)
	}

	@DeleteMapping("/cache")
	fun deleteFromCache(@RequestParam site: String): Boolean {
		if(site.isBlank())
			throw BadRequestResponse("'site' parameter must not be blank")
		return loader.deleteFromCache(site)
	}

	@GetMapping("/cache")
	fun getCache(): List<Pair<String, Int>> = loader.peekCache()
}
