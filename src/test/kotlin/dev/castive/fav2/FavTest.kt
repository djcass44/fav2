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

import dev.castive.fav2.config.AppConfig
import dev.castive.log2.Log
import dev.dcas.util.extend.safe
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.endsWith
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito
import java.awt.image.BufferedImage

class FavTest {
	private val cache = TimedCache<String, BufferedImage>()

	private val appConfig: AppConfig = Mockito.mock(AppConfig::class.java)

	@BeforeEach
	internal fun setUp() {
		Mockito.`when`(appConfig.url).thenReturn("http://localhost:8080")
	}

	@ParameterizedTest
	@ValueSource(strings = [
		"https://github.com",
		"https://google.com",
		"https://apple.com"
	])
	fun getKnown(value: String) {
		val icon = Fav(cache = cache, appConfig = appConfig).loadDomain(value, skipDownload = true)
		Log.i(javaClass, "Icon: $icon")
		assertNotNull(icon)
		assertThat(icon, endsWith(value.safe()))
	}

	@Test
	fun checkSecure() {
		val fav = Fav(cache = cache, appConfig = appConfig)
		val allowed = fav.checkDomain("https://google.com")
		assertThat(allowed, `is`(true))
	}
	@Test
	fun checkInsecure() {
		val fav = Fav(cache =  cache, appConfig = appConfig)
		val allowed = fav.checkDomain("http://google.com")
		assertThat(allowed, `is`(false))
	}
}
