package com.example.springdata.repository

import com.example.springdata.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository : JpaRepository<UserEntity, String> {

    @Query("select user from UserEntity user where user.email = :email")
    fun findByEmail(@Param("email") email: String): List<UserEntity>

    @Query("select user from UserEntity user where user.name = :name")
    fun findByName(@Param("name") name: String)
}