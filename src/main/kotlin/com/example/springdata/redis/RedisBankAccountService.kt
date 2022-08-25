package com.example.springdata.redis

import com.example.springdata.entity.BankAccount
import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RedisBankAccountService {

    fun findById(accountId: ObjectId): Mono<BankAccount>

    fun getAll(): Flux<BankAccount>

    fun deleteAccount(bankAccountId: String): Mono <Void>
    fun deposit(accountId: ObjectId, depositAmount: Double): Mono<BankAccount>
}