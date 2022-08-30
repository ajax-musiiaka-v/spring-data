package com.example.springdata.redis

import com.example.springdata.entity.BankAccount
import com.google.gson.*
import org.bson.types.ObjectId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.lang.reflect.Type

@Configuration
class RedisConfiguration {

    @Bean
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        return LettuceConnectionFactory()
    }

    @Bean // serialize
    fun redisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, BankAccount> {

        val context: RedisSerializationContext<String, BankAccount> =
            RedisSerializationContext
                .newSerializationContext<String, BankAccount>(StringRedisSerializer())
                .key(StringRedisSerializer())
                .value(BankAccountSerializer())
                .hashKey(StringRedisSerializer())
                .hashValue(BankAccountSerializer())
                .build()

        return ReactiveRedisTemplate(factory, context)
    }
}

class BankAccountSerializer : RedisSerializer<BankAccount> {
    override fun serialize(t: BankAccount?): ByteArray {
        return GsonBuilder().registerTypeAdapter(ObjectId::class.java, ObjectIdSerializer())
            .create().toJson(t).toByteArray()
    }

    override fun deserialize(bytes: ByteArray?): BankAccount? {
        if(bytes == null) return null

        return GsonBuilder().registerTypeAdapter(ObjectId::class.java, ObjectIdDeserializer())
            .create().fromJson(String(bytes), BankAccount::class.java)
    }
}

class ObjectIdSerializer : JsonSerializer<ObjectId> {
    override fun serialize(id: ObjectId, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObj = JsonObject()
        jsonObj.add("id", JsonPrimitive(id.toString()))

        return jsonObj
    }
}

class ObjectIdDeserializer : JsonDeserializer<ObjectId> {
    override fun deserialize(jsonElement: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ObjectId {
        val jsonObject = jsonElement.asJsonObject

        return ObjectId(jsonObject.get("id").asString)
    }
}
