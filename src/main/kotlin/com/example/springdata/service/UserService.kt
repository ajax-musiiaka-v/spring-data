package com.example.springdata.service

import com.example.springdata.entity.UserEntity

interface UserService {

    fun createUser(name: String, email: String): String

    fun getAll(): Collection<UserEntity>

    fun deleteUser(id: String)
}