package com.example.springdata.service

import com.example.springdata.entity.BankAccount
import com.example.springdata.entity.User
import com.example.springdata.repository.BankAccountRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.UnsupportedOperationException

@Service
class BankAccountServiceImpl(private val accountRepository: BankAccountRepository,
                             private val userService: UserService
                             ) : BankAccountService {

    override fun getAll(): Flux<BankAccount> =
        accountRepository.findAll()

    override fun createBankAccount(userId: ObjectId, accountName: String): Mono<BankAccount> {

        var userMono: Mono<User> = userService.findById(userId)

        val savedAccount: Mono<BankAccount> =
            accountRepository.save(BankAccount(name = accountName))

        userMono = userMono
            .zipWith(savedAccount)
            .flatMap { userService.updateUser(  // t1 refers to user. t2 to account
                it.t1.apply {
                    it.t1.bankAccountId = it.t2.id }
            ) }

        return savedAccount
    }

    override fun deleteAccount(bankAccountId: String): Mono<Void> {
        val objectId: ObjectId = ObjectId(bankAccountId)

        userService.findByBankAccountId(objectId).flatMap {
            userService.updateUser(
                it.apply { it.bankAccountId = null }
        ) }

        return accountRepository.deleteById(objectId)
    }

    override fun deposit(accountId: ObjectId,
                         depositAmount: Double): Mono<BankAccount> {
        if (depositAmount < 0) throw UnsupportedOperationException("Negative deposit amount")

        return getAccount(accountId).flatMap {
            accountRepository.save(
                it.apply { it.balance += depositAmount }
            )
        }
    }

    override fun withdraw(accountId: ObjectId,
                          withdrawAmount: Double): Mono<BankAccount> {
        if (withdrawAmount < 0)
            throw UnsupportedOperationException("Negative withdraw amount")

        return getAccount(accountId).flatMap {
            account ->
                if (account.balance < withdrawAmount)
                    throw UnsupportedOperationException("Withdraw amount less than account balance")
                else accountRepository.save(
                    account.apply { account.balance -= withdrawAmount }
            )
        }
    }

    override fun transfer(accountFromId: ObjectId,
                          transferAmount: Double,
                          accountToId: ObjectId): Flux<BankAccount> {
        if (transferAmount < 0)
            throw UnsupportedOperationException("Negative transfer amount")

        return Flux
                .merge(withdraw(accountFromId, transferAmount), deposit(accountToId, transferAmount))

    }

    private fun getAccount(accountId: ObjectId): Mono<BankAccount> =
        accountRepository.findById(accountId)

}