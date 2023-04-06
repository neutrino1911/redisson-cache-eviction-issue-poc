package org.example

import org.awaitility.kotlin.await
import org.example.RedissonSpringCacheManagerFactory.CACHE_3_18_1
import org.junit.jupiter.api.Test
import org.redisson.api.RMapCache
import org.springframework.cache.CacheManager
import java.time.Duration

class Redisson3_18_1Test {

    val cacheManager: CacheManager = RedissonSpringCacheManagerFactory.build()

    @Test
    fun `should evict superfluous elements`() {
        val cache = cacheManager.getCache(CACHE_3_18_1)!!
        log.info { "Before invalidate" }
        RedissonClientHolder.redissonClient.printKeys()
        cache.invalidate()
        log.info { "After invalidate" }
        RedissonClientHolder.redissonClient.printKeys()

        repeat(times = 11) {
            cache.put(it.toString(), it.toString())
        }

        val rMapCache = cache.nativeCache as RMapCache<*, *>
        log.info { "${cache.name} size: ${rMapCache.size}" }

        await.pollInterval(Duration.ofMillis(100)).atMost(Duration.ofSeconds(10)).until {
            rMapCache.size == 10
        }
    }
}
