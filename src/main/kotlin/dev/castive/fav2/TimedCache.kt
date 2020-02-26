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

package dev.castive.fav2

import dev.castive.log2.loga
import dev.castive.log2.loge
import dev.castive.log2.logi
import dev.castive.log2.logv
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.timer

class TimedCache<K, V>(
	private val ageLimit: Int = 30,
	tickDelay: Long = 10_000L,
	var listener: TimedCacheListener<K, V>? = null
) {
	interface TimedCacheListener<K, V> {
		suspend fun onAgeLimitReached(key: K, value: V)
	}

	private val cache = ConcurrentHashMap<K, Pair<Int, V>>()

	operator fun get(key: K): V? {
		val item = cache[key]
		// reset the clock when accessed
		if(item != null)
			set(key, item.second)
		return item?.second
	}

	operator fun set(key: K, value: V) {
		cache[key] = 0 to value
	}

	fun peek(): List<Pair<K, Int>> = cache.map {
		it.key to it.value.first
	}

	fun remove(key: K): Boolean = try {
		cache.remove(key)
		true
	}
	catch (e: Exception) {
		"Failed to remove item from cache with key: $key".loge(javaClass, e)
		false
	}

	init {
		// start the task
		"Starting timer: ${TimedCache::class.java.name} @ ${System.currentTimeMillis()}".logi(TimedCache::class.java)
		timer(TimedCache::class.java.name, true, 0, tickDelay) {
			GlobalScope.launch {
				onTick()
			}
		}
		Runtime.getRuntime().addShutdownHook(Thread {
			runBlocking {
				// tell the cache to move all items to disc
				onClose()
			}
		})
	}

	/**
	 * Increment the cache age by 1
	 * Items which are considered 'too old' are removed
	 */
	private suspend fun onTick() = withContext(Dispatchers.Default) {
		"Tick for ${TimedCache::class.java.name} @ ${System.currentTimeMillis()}".logv(TimedCache::class.java)
		val staleItems = arrayListOf<K>()
		cache.forEach { (t: K, u: Pair<Int, V>) ->
			cache[t] = (u.first + 1) to u.second
			// check the age
			if(u.first >= ageLimit) {
				// tell the listener that the item is too old
				launch {
					listener?.onAgeLimitReached(t, u.second)
				}
				staleItems.add(t) // track old items for later so we don't run into concurrency issues
			}
		}
		"Scanned ${cache.size} items, found ${staleItems.size} stale item(s)".logi(TimedCache::class.java)
		// remove the old entries
		staleItems.forEach {
			cache.remove(it)
		}
		if(staleItems.size > 0)
			"Removed ${staleItems.size} items".logi(javaClass)
	}

	private suspend fun onClose() = withContext(Dispatchers.Default) {
		"Purging TimedCache, this may take a moment".loga(javaClass)
		"Purging ${cache.size} items [reason: SHUTDOWN]".logi(javaClass)
		cache.forEach { (t: K, u: Pair<Int, V>) ->
			listener?.onAgeLimitReached(t, u.second)
		}
	}
}
