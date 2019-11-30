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

package dev.castive.fav2.net

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class JsoupNetworkLoaderTest {
	private val loader = JsoupNetworkLoader()


	@ParameterizedTest
	@CsvSource(value = [
		"https://reddit.com,//reddit.com/favicon.png,https://reddit.com/favicon.png",
		"https://github.com,//github.com/images/favicon.png,https://github.com/images/favicon.png"
	])
	fun `double slash start is corrected`(domain: String, imageUrl: String, expected: String) {
		assertThat(loader.getAbsoluteUrl(domain, imageUrl), `is`(expected))
	}

	@ParameterizedTest
	@CsvSource(value = [
		"https://reddit.com,/favicon.png,https://reddit.com/favicon.png",
		"https://github.com,/images/favicon.png,https://github.com/images/favicon.png"
	])
	fun `single slash start is corrected`(domain: String, imageUrl: String, expected: String) {
		assertThat(loader.getAbsoluteUrl(domain, imageUrl), `is`(expected))
	}

	@ParameterizedTest
	@CsvSource(value = [
		"https://reddit.com,favicon.png,https://reddit.com/favicon.png",
		"https://github.com,images/favicon.png,https://github.com/images/favicon.png"
	])
	fun `no http scheme is corrected`(domain: String, imageUrl: String, expected: String) {
		assertThat(loader.getAbsoluteUrl(domain, imageUrl), `is`(expected))
	}
}
