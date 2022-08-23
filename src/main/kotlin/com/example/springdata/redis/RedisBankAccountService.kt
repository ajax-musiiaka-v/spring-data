package com.example.springdata.redis

import com.example.springdata.entity.BankAccount
import com.example.springdata.service.BankAccountService
import org.bson.types.ObjectId
import org.springframework.data.redis.core.ReactiveHashOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.data.repository.CrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface RedisBankAccountService {

    fun findById(accountId: ObjectId): Mono<BankAccount>

    fun getAll(): Flux<BankAccount>
}