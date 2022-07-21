package com.example.springdata.service

import com.example.springdata.entity.BankAccount
import com.example.springdata.entity.User
import com.example.springdata.repository.BankAccountRepository
import com.example.springdata.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.lang.UnsupportedOperationException

@Service
class BankAccountServiceImpl(private val accountRepository: BankAccountRepository,
                             private val userRepository: UserRepository) : BankAccountService {

    override fun getAll(): Collection<BankAccount> =
        accountRepository.findAll()

    override fun createBankAccount(userId: ObjectId, accountName: String): BankAccount {
        val account = BankAccount(
            name = accountName
        )

        val savedAccount = accountRepository.save(account)
        val user = userRepository.findById(userId).get()
        user.bankAccountId = savedAccount.id
        userRepository.save(user)

        return savedAccount
    }

    override fun deleteAccount(bankAccountId: ObjectId) {
        val user: User? = userRepository.findByBankAccountId(bankAccountId)
        if(user != null) {
            user.bankAccountId = null
            userRepository.save(user)
        }
        accountRepository.deleteById(bankAccountId)
    }

    override fun deposit(accountId: ObjectId,
                         depositAmount: Double): BankAccount{
        if (depositAmount < 0) throw UnsupportedOperationException("Negative deposit amount")

        val account = getAccount(accountId)
        account.balance += depositAmount

        return accountRepository.save(account)
    }

    override fun withdraw(accountId: ObjectId,
                          withdrawAmount: Double): BankAccount {
        if (withdrawAmount < 0)
            throw UnsupportedOperationException("Negative withdraw amount")

        val account = getAccount(accountId)
        if (account.balance < withdrawAmount)
            throw UnsupportedOperationException("Withdraw amount less than account balance")

        account.balance -= withdrawAmount

        return accountRepository.save(account)
    }

    override fun transfer(accountFromId: ObjectId,
                          transferAmount: Double,
                          accountToId: ObjectId): List<BankAccount> {
        if (transferAmount < 0)
            throw UnsupportedOperationException("Negative transfer amount")

        val accountFrom = getAccount(accountFromId)
        if (accountFrom.balance < transferAmount)
            throw  UnsupportedOperationException("Transfer amount less than account balance")

        val accountTo = getAccount(accountToId)

        accountFrom.balance -= transferAmount
        accountTo.balance += transferAmount

        return accountRepository.saveAll(listOf(accountFrom, accountTo))
    }

    private fun getAccount(accountId: ObjectId) =
        accountRepository.findById(accountId).get()
}