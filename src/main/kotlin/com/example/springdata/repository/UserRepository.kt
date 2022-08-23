package com.example.springdata.repository

import com.example.springdata.entity.User
import org.bson.types.ObjectId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : ReactiveCrudRepository<User, ObjectId> {

    fun findByEmail(email: String): Mono<User>

    fun findByName(name: String): Mono<User>

    fun findByBankAccountId(bankAccountId: ObjectId):Mono<User>

}