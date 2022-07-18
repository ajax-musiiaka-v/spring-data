package com.example.springdata.repository

import com.example.springdata.entity.UserEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<UserEntity, String> {

    fun findByEmail(@Param("email") email: String): List<UserEntity>

    fun findByName(@Param("name") name: String)
}