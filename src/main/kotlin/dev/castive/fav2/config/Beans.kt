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

package dev.castive.fav2.config

import dev.castive.fav2.TimedCache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import java.awt.image.BufferedImage
import java.net.HttpURLConnection

@Configuration
class Beans @Autowired constructor(
	private val cacheConfig: CacheConfig
) {

	@ConditionalOnMissingBean
	@Bean
	fun restTemplate(): RestTemplate = RestTemplate(
		object : SimpleClientHttpRequestFactory() {
			override fun prepareConnection(connection: HttpURLConnection, httpMethod: String) {
				super.prepareConnection(connection, httpMethod)
				connection.instanceFollowRedirects = true
			}
		}
	)

	@ConditionalOnMissingBean
	@Bean
	fun timedCache(): TimedCache<String, BufferedImage> = TimedCache(
		cacheConfig.limit,
		cacheConfig.delay
	)
}
