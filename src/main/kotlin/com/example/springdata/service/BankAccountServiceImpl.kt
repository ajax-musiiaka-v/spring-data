package com.example.springdata.service

import com.example.springdata.entity.BankAccount
import com.example.springdata.repository.BankAccountRepository
import com.example.springdata.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.lang.UnsupportedOperationException

@Service
class BankAccountServiceImpl(private val accountRepository: BankAccountRepository,
                             private val userRepository: UserRepository) : BankAccountService {

    override fun createBankAccount(userId: ObjectId, accountName: String): BankAccount {
        val account = BankAccount(
            holderId = userId,
            name = accountName
        )

        val savedAccount = accountRepository.save(account)

        return savedAccount
    }

    override fun deleteAccount(accountId: ObjectId) {
        val holderId: ObjectId? = accountRepository.findById(accountId).get().holderId
        if (holderId != null) {
            userRepository.findById(holderId).get().bankAccountId = null
        }
        accountRepository.deleteById(accountId)
    }

    override fun deposit(accountId: ObjectId, depositAmount: Double) {
        val account = getAccount(accountId)
        account.balance += depositAmount
        accountRepository.save(account)
    }

    override fun withdraw(accountId: ObjectId, withdrawAmount: Double) {
        val account = getAccount(accountId)
        if (account.balance < withdrawAmount)
            throw UnsupportedOperationException("Withdraw amount less than account balance")
        account.balance -= withdrawAmount
        accountRepository.save(account)
    }

    override fun transfer(accountFromId: ObjectId, transferAmount: Double, accountToId: ObjectId) {
        val accountFrom = getAccount(accountFromId)
        if (accountFrom.balance < transferAmount)
            throw  UnsupportedOperationException("Transfer amount less than account balance")

        val accountTo = getAccount(accountToId)
        accountFrom.balance -= transferAmount
        accountTo.balance += transferAmount

        accountRepository.save(accountFrom)
        accountRepository.save(accountTo)
    }

    private fun getAccount(accountId: ObjectId) =
        accountRepository.findById(accountId).get()
}