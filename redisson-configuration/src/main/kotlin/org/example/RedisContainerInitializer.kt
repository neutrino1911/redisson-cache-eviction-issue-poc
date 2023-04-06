package org.example

import org.testcontainers.containers.GenericContainer

class RedisContainerWrapper(dockerImageName:String) : GenericContainer<RedisContainerWrapper>(dockerImageName)

object RedisContainerInitializer {
    val redisAddress: String by lazy {
        val dockerImageName = "redis:5.0.7-alpine"
        val exposedPort = 6379

        val container = RedisContainerWrapper(dockerImageName).withExposedPorts(exposedPort)
        container.start()

        "redis://${container.host}:${container.getMappedPort(exposedPort)}"
    }
}
