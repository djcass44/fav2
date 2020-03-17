/*
 *  Copyright 2020 Django Cass
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

package dev.castive.fav2.service

import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.*
import javax.imageio.ImageIO

@Service
class ImageUtils {

	/**
	 * Convert a BufferedImage into Base64
	 */
	fun biToBase64(img: RenderedImage, format: String = "png"): String {
		val os = ByteArrayOutputStream()
		ImageIO.write(img, format, Base64.getEncoder().wrap(os))
		return os.toString(StandardCharsets.ISO_8859_1.name())
	}

	/**
	 * Convert Base64 encoded image to a BufferedImage
	 */
	fun base64ToBi(data: String): BufferedImage {
		return ImageIO.read(ByteArrayInputStream(
			Base64.getDecoder().decode(data)
		))
	}

}
