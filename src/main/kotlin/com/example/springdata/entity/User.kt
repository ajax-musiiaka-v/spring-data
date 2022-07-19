package com.example.springdata.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class User(
    @Id internal val id: ObjectId? = null,
    internal var name: String,
    internal var email: String,
    internal var address: Address,
    internal var enabled: Boolean? = null
)
