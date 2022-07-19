package com.example.springdata.service

import com.example.springdata.entity.User

interface UserService {

//    fun createUser(name: String, email: String): String
    fun createUser(name: String, email: String): User

    fun getAll(): Collection<User>

    fun deleteUser(id: String)
}