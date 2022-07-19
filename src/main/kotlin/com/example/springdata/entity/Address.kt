package com.example.springdata.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "addresses")
data class Address(
    @Id internal val id: ObjectId? = null,
    internal var street: String,
    internal var city: String,
)