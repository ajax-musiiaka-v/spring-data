package com.example.springdata.repository

import com.example.springdata.entity.BankAccount
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface BankAccountRepository : ReactiveMongoRepository<BankAccount, ObjectId>
