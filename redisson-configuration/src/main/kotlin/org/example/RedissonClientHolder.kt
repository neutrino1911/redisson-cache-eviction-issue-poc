package org.example

import mu.KotlinLogging
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.client.codec.StringCodec
import org.redisson.codec.JsonJacksonCodec
import org.redisson.config.Config
import org.redisson.spring.cache.CacheConfig
import org.redisson.spring.cache.RedissonSpringCacheManager
import kotlin.time.Duration.Companion.seconds

val log = KotlinLogging.logger {}

object RedissonClientHolder {
    val redissonClient: RedissonClient by lazy {
        val config = Config().apply {
            useSingleServer().address = RedisContainerInitializer.redisAddress
            minCleanUpDelay = 1
            maxCleanUpDelay = 1
        }
        Redisson.create(config)
    }
}

object RedissonSpringCacheManagerFactory {

    const val CACHE_3_18_1 = "cache-3.18.1"
    const val CACHE_3_19_1 = "cache-3.19.0"
    const val CACHE_LATEST = "cache-latest"

    fun build(): RedissonSpringCacheManager {
        val redissonClient = RedissonClientHolder.redissonClient

        val cacheConfig = CacheConfig().apply {
            ttl = 30.seconds.inWholeMilliseconds
            maxIdleTime = 0
            maxSize = 10
        }

        val configMap = mapOf(
            CACHE_3_18_1 to cacheConfig,
            CACHE_3_19_1 to cacheConfig,
            CACHE_LATEST to cacheConfig,
        )

        val jsonJacksonCodec = JsonJacksonCodec()
//        val codec = CompositeCodec(StringCodec.INSTANCE, jsonJacksonCodec, jsonJacksonCodec)
        val codec = StringCodec.INSTANCE

        return RedissonSpringCacheManager(redissonClient, configMap, codec)
    }
}

fun RedissonClient.printKeys() {
    keys.keys.forEach {
        log.info { it }
    }
}
