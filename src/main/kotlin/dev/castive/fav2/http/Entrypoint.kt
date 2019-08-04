package dev.castive.fav2.http

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    // Start the HTTP server
    launch { App().start() }.join()
}