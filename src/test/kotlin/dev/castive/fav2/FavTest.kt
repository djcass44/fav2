/*
 *    Copyright 2019 Django Cass
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
 *
 */

package dev.castive.fav2

import dev.castive.log2.Log
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class FavTest {
	@ParameterizedTest
	@ValueSource(strings = [
		"https://github.com",
		"https://google.com",
		"https://apple.com"
	])
	fun getKnown(value: String) {
		val icon = Fav().loadDomain(value, skipDownload = true)
		Log.i(javaClass, "Icon: $icon")
		assertNotNull(icon)
		assertTrue(icon!!.endsWith(URLEncoder.encode(value, StandardCharsets.UTF_8)))
	}

	@Test
	fun checkSecure() {
		val fav = Fav(debug = false)
		val allowed = fav.checkDomain("https://google.com")
		assertTrue(allowed)
	}
	@Test
	fun checkInsecure() {
		val fav = Fav(debug = false)
		val allowed = fav.checkDomain("http://google.com")
		assertFalse(allowed)
	}
	@Test
	fun checkInsecureAllowed() {
		val fav = Fav(debug = false)
		val allowed = fav.checkDomain("http://google.com")
		assertTrue(allowed)
	}
}