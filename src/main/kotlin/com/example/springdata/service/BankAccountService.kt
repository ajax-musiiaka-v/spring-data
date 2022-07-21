package com.example.springdata.service

import com.example.springdata.entity.BankAccount
import org.bson.types.ObjectId

interface BankAccountService {
    fun createBankAccount(userId: ObjectId, accountName: String = "default"): BankAccount

    fun getAll(): Collection<BankAccount>

    fun deleteAccount(bankAccountId: ObjectId)

    fun deposit(accountId: ObjectId, depositAmount: Double): BankAccount

    fun withdraw(accountId: ObjectId, withdrawAmount: Double): BankAccount

    fun transfer(accountFromId: ObjectId,
                 transferAmount: Double,
                 accountToId: ObjectId) : List<BankAccount>

}