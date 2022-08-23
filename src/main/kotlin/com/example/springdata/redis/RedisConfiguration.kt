package com.example.springdata.redis

import com.example.springdata.entity.BankAccount
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.*


@Configuration
//@EnableRedisRepositories
class RedisConfiguration {
//    private val entryTtl = Duration.ofSeconds(30L)

//    @Bean // serialize as list
//    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, BankAccount> {
//        val keySerializer = StringRedisSerializer()
//        val valueSerializer = Jackson2JsonRedisSerializer(BankAccount::class.java)
//        val contextBuilder: RedisSerializationContext.RedisSerializationContextBuilder<String, BankAccount> =
//            RedisSerializationContext.newSerializationContext(keySerializer)
//        val context = contextBuilder.value(valueSerializer).build()
//
//        return ReactiveRedisTemplate(factory, context)
//    }

    @Bean // serialize as hash
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, BankAccount> {
        val contextBuilder: RedisSerializationContext.RedisSerializationContextBuilder<String, BankAccount> =
            RedisSerializationContext.newSerializationContext(StringRedisSerializer())

        val context = contextBuilder
            .key(StringRedisSerializer())
            .value(GenericToStringSerializer(BankAccount::class.java))
            .hashKey(StringRedisSerializer())
            .hashValue(GenericJackson2JsonRedisSerializer())
            .build()

        return ReactiveRedisTemplate(factory, context)
    }

    @Bean
    fun redisCacheManagerBuilderCustomizer(): RedisCacheManagerBuilderCustomizer {
        return RedisCacheManagerBuilderCustomizer { builder: RedisCacheManagerBuilder ->
            builder
                .withCacheConfiguration(
                    "accounts_cache",
                    RedisCacheConfiguration.defaultCacheConfig()
//                        .entryTtl(Duration.ofMinutes(10))
                )
        }
    }

//    @Bean
//    fun keyCommands(factory: ReactiveRedisConnectionFactory): ReactiveKeyCommands {
//        return factory.reactiveConnection
//            .keyCommands()
//    }
//
//    @Bean
//    fun stringCommands(factory: ReactiveRedisConnectionFactory): ReactiveStringCommands {
//        return factory.reactiveConnection
//            .stringCommands()
//    }

//    @Bean
//    fun cacheConfig(): RedisCacheConfiguration {
//        return RedisCacheConfiguration.defaultCacheConfig()
//            .entryTtl(entryTtl)
//            .serializeValuesWith(SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer()))
//    }
}