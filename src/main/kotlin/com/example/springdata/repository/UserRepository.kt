package com.example.springdata.repository

import com.example.springdata.entity.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, ObjectId> {

    @Query("{'email' : ?0}")
    fun findByEmail(email: String): List<User>

    @Query("{'name' : ?0}")
    fun findByName(name: String): List<User>

    @Query("{'_id' : ?0}")
    fun findById(id: String): User
}