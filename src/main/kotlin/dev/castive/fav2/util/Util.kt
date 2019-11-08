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

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Convert a string to be URL safe
 */
fun String.safe(): String = URLEncoder.encode(this, StandardCharsets.UTF_8)

/**
 * Load the environment variable by this string
 */
fun String.env(default: String = ""): String = EnvUtil.getEnv(this, default)

fun String.asEnv(default: String = "") = EnvUtil.getEnv(this, default)
