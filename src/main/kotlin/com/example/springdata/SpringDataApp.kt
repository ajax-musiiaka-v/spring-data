package com.example.springdata

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication(exclude = [MongoAutoConfiguration::class, MongoDataAutoConfiguration::class, SpringDataWebAutoConfiguration::class,
	RedisAutoConfiguration::class, RedisRepositoriesAutoConfiguration::class])
@EnableMongoRepositories(basePackages=["com.example.springdata.repository"])
class SpringDataApp

fun main(args: Array<String>) {
	runApplication<SpringDataApp>(*args)
}
