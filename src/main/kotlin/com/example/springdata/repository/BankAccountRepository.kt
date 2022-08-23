package com.example.springdata.repository

import com.example.springdata.entity.BankAccount
import org.bson.types.ObjectId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BankAccountRepository : ReactiveCrudRepository<BankAccount, ObjectId>
