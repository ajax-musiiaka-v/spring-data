package com.example.springdata.service

import com.example.springdata.entity.BankAccount
import com.example.springdata.entity.User
import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface BankAccountService {
    fun createBankAccount(userId: ObjectId, accountName: String = "default"): Mono<BankAccount>

    fun getAll(): Flux<BankAccount>

    fun deleteAccount(bankAccountId: String): Mono <Void>

    fun deposit(accountId: ObjectId, depositAmount: Double): Mono<BankAccount>

    fun withdraw(accountId: ObjectId, withdrawAmount: Double): Mono<BankAccount>

    fun transfer(accountFromId: ObjectId,
                 transferAmount: Double,
                 accountToId: ObjectId) : Flux<BankAccount>

}