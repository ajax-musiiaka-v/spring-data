package com.example.springdata.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class User(
    @Id internal val id: ObjectId? = null,
    internal var name: String,
    @Indexed(unique = true) internal var email: String,
    internal var addressId: ObjectId? = null,
    internal var enabled: Boolean? = null,
    internal var bankAccountId: ObjectId? = null,
    @Version internal var version: Long? = null
)
