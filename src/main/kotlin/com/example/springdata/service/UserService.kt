package com.example.springdata.service

import com.example.springdata.entity.User

interface UserService {

//    fun createUser(name: String, email: String): String
    fun createUser(name: String, email: String): User

    fun getAll(): Collection<User>

    fun findByName(name: String): User?

    fun findByEmail(email: String): User?

    fun deleteUser(id: String)
}