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

import dev.castive.fav2.service.IconLoader
import dev.dcas.util.extend.isESNullOrBlank
import dev.dcas.util.spring.responses.BadRequestResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@RestController
class IconController(private val loader: IconLoader) {

	@CrossOrigin("*")
	@GetMapping("/icon")
	suspend fun getImage(@RequestParam site: String): ResponseEntity<ByteArray> {
		if(site.isESNullOrBlank())
			throw BadRequestResponse("'site' parameter must not be blank")
		// get the stream or throw 400
		val data = loader.getIconFromUrl(site) ?: error("Failed to locate icon at url: '$site'")
		val headers = HttpHeaders().apply {
			contentType = MediaType.IMAGE_PNG
		}
		// convert the image to a bytearray
		val baos = ByteArrayOutputStream()
		ImageIO.write(data, "png", baos)
		return ResponseEntity(baos.toByteArray(), headers, HttpStatus.OK)
	}
}
