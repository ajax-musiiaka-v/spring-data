package com.example.springdata.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "bank_accounts")
data class BankAccount (
    @Id internal var id: ObjectId? = null,
    internal var bankAccountName: String = "default",
    internal var balance: Double = 0.0,
    @Version internal var version: Long? = null
)
