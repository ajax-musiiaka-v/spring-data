package com.example.springdata.repository

import com.example.springdata.entity.BankAccount
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface BankAccountRepository : MongoRepository<BankAccount, ObjectId>
