package com.example.springdata.repository

import com.example.springdata.entity.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, ObjectId> {

    fun findByEmail(email: String): User?

    fun findByName(name: String): User?

    fun findByBankAccountId(bankAccountId: ObjectId): User?

}