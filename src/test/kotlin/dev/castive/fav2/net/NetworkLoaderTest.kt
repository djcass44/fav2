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

package dev.castive.fav2.net

import dev.castive.log2.Log
import org.hamcrest.CoreMatchers.anyOf
import org.hamcrest.CoreMatchers.endsWith
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.web.client.RestTemplate

class NetworkLoaderTest {

	@ParameterizedTest
	@ValueSource(strings = [
		"https://github.com"
	])
	fun getKnownDocument(value: String) {
		val loader = JsoupNetworkLoader()
		val icon = loader.getIconPath(value)
		Log.d(javaClass, icon.toString())
		assertNotNull(icon)
		assertTrue(icon!!.endsWith("png") || icon.endsWith("ico"))
		assertThat(icon, anyOf(endsWith("png"), endsWith("ico")))
	}
	@ParameterizedTest
	@ValueSource(strings = [
		"https://google.com",
		"https://apple.com"
	])
	fun getFailDocument(value: String) {
		val loader = JsoupNetworkLoader()
		val icon = loader.getIconPath(value)
		Log.d(javaClass, icon.toString())
		assertNull(icon)
	}
	@ParameterizedTest
	@ValueSource(strings = [
		"https://github.com",
		"https://google.com",
		"https://apple.com",
		"https://hub.docker.com"
	])
	fun getKnownDirect(value: String) {
		val loader = DirectNetworkLoader(RestTemplate())
		val icon = loader.getIconPath(value)
		Log.d(javaClass, icon)
		assertNotNull(icon)
		assertThat(icon, anyOf(endsWith("png"), endsWith("ico")))
	}
}
