package com.example.springdata.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection="addresses")
class AddressEntity {

    @Id
    internal lateinit var id: String

    internal lateinit var street: String

    internal lateinit var city: String
}