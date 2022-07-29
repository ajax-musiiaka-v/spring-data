package com.example.springdata.service

import com.example.springdata.entity.User
import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserService {
    fun createUser(name: String, email: String): Mono<User>

    fun getAll(): Flux<User>

    fun findByName(name: String): Mono<User>

    fun findByEmail(email: String): Mono<User>

    fun findById(userId: ObjectId): Mono<User>

    fun updateUser(user: User): Mono<User>

    fun deleteUser(userId: String): Mono<Void>

    fun findByBankAccountId(bankAccountId: ObjectId): Mono<User>
}