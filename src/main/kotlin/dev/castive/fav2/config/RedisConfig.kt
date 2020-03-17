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


import dev.castive.fav2.entity.Icon
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

@Configuration
class RedisConfig {

	@Value("\${spring.redis.host:localhost}")
	lateinit var redisHost: String

	@ConditionalOnMissingBean
	@Bean
	fun jedisConnectionFactory() = JedisConnectionFactory(
		RedisStandaloneConfiguration(redisHost)
	)

	@ConditionalOnMissingBean
	@Bean
	fun redisTemplate(): RedisTemplate<String, Icon> = RedisTemplate<String, Icon>().apply {
		setConnectionFactory(jedisConnectionFactory())
	}

}
