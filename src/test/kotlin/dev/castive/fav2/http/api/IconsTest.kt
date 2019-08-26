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

package dev.castive.fav2.http.api

import io.javalin.http.BadRequestResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class IconsTest {
    /**
     * test that a secure-url with a backslash ending is exactly the same
     */
    @Test
    fun `test valid secure url with backslash`() {
        val url = "https://google.com/"
        val bestUrl = Icons().getBestUrl(url)
        Assertions.assertEquals(url, bestUrl)
    }

    /**
     * test that a secure-url without a backslash has one appended to the end
     */
    @Test
    fun `test valid secure url without backslash`() {
        val url = "https://google.com"
        val bestUrl = Icons().getBestUrl(url)
        Assertions.assertEquals("${url}/", bestUrl)
    }

    /**
     * test that a url without a scheme is given the https:// prefix
     */
    @Test
    fun `test secure url without scheme`() {
        val url = "google.com"
        val bestUrl = Icons().getBestUrl(url)
        Assertions.assertEquals("https://google.com/", bestUrl)
    }

    /**
     * test that an insecure url (http://) is rejected
     */
    @Test
    fun `test insecure url`() {
        val url = "http://google.com"
        assertThrows<BadRequestResponse> {
            Icons().getBestUrl(url)
        }
    }
}