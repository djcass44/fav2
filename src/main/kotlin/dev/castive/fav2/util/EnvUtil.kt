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

package dev.castive.fav2.util

internal object EnvUtil {
    fun getEnv(name: String, default: String = ""): String {
        val env = System.getenv(name)
        return if (env.isNullOrEmpty()) default else env
    }

    const val FAV_ALLOW_CORS = "FAV_ALLOW_CORS"
    const val FAV_DEBUG = "FAV_DEBUG"
    const val FAV_HTTP_PORT = "FAV_HTTP_PORT"
	const val FAV_DATA = "FAV_DATA"
    const val FAV_BASE_URL = "FAV_BASE_URL"

    const val FAV_CACHE_TICK = "FAV_CACHE_TICK"
    const val FAV_CACHE_LIMIT = "FAV_CACHE_LIMIT"
}