package com.example.springdata.service

import com.example.springdata.entity.BankAccount
import com.example.springdata.repository.BankAccountRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.lang.UnsupportedOperationException

@Service
class BankAccountServiceImpl(private val accountRepository: BankAccountRepository,
                             private val userService: UserService
                             ) : BankAccountService {

    override fun getAll(): Collection<BankAccount> =
        accountRepository.findAll()

    override fun createBankAccount(userId: ObjectId, accountName: String): BankAccount {
        val user = userService.findById(userId) ?:
                            throw NoSuchElementException("User with id $userId not found")

        val account = BankAccount(name = accountName)
        val savedAccount = accountRepository.save(account)

        user.bankAccountId = savedAccount.id
        userService.updateUser(user)

        return savedAccount
    }

    override fun deleteAccount(bankAccountId: String) {
        val objectId = ObjectId(bankAccountId)
        val user = userService.findByBankAccountId(objectId) ?:
                throw NoSuchElementException("User with bank account id $bankAccountId not found")

        user.bankAccountId = null
        userService.updateUser(user)
        accountRepository.deleteById(objectId)
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

    private fun getAccount(accountId: ObjectId): BankAccount =
        accountRepository.findById(accountId)
            .orElseThrow { NoSuchElementException("Bank account with id $accountId not found") }
}