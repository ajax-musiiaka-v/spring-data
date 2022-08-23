package com.example.springdata.redis

import com.example.springdata.entity.BankAccount
import org.bson.types.ObjectId
import org.springframework.data.redis.core.ReactiveHashOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class RedisBankAccountServiceImpl(
    private val redisTemplate: ReactiveRedisTemplate<String, BankAccount>
)
    : RedisBankAccountService {

    private val hashOps: ReactiveHashOperations<String, String, BankAccount> = redisTemplate.opsForHash()
    private val KEY = "bank_accounts"

    override fun findById(accountId: ObjectId): Mono<BankAccount> {
        return hashOps.get(KEY, accountId)
    }

    override fun getAll(): Flux<BankAccount> {
       return hashOps.values(KEY)
    }
}