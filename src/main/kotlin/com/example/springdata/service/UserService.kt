package com.example.springdata.service

import com.example.springdata.entity.User
import org.bson.types.ObjectId

interface UserService {
    fun createUser(name: String, email: String): User

    fun getAll(): Collection<User>

    fun findByName(name: String): User?

    fun findByEmail(email: String): User?

    fun findById(userId: ObjectId): User?

    fun updateUser(user: User): User
    fun deleteUser(id: String)

    fun findByBankAccountId(bankAccountId: ObjectId): User?
}