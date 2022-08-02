package com.example.springdata.service

import com.example.springdata.entity.BankAccount
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

        return accountRepository
            .save(BankAccount(name = accountName))
            .zipWith(userService.findById(userId))
            .flatMap {      // it.t1 - bankAccount, it.t2 - user
                userService
                    .updateUser(it.t2.copy(bankAccountId = it.t1.id))
                    .then(Mono.just(it.t1))
            }
    }

    override fun deleteAccount(bankAccountId: String): Mono<Void> {
        val objectId = ObjectId(bankAccountId)

        return accountRepository
            .deleteById(objectId)
            .switchIfEmpty(userService.findByBankAccountId(objectId)
                .flatMap {
                    userService
                        .updateUser(it.copy(bankAccountId = null))
                        .then(Mono.empty())
                })
    }

    override fun deposit(accountId: ObjectId,
                         depositAmount: Double): Mono<BankAccount> {
        if (depositAmount < 0) {
            return Mono.error(UnsupportedOperationException("Negative deposit amount"))
        }

        return findById(accountId).flatMap {
            accountRepository.save(
                it.copy(balance = it.balance + depositAmount)
            )
        }
    }

    override fun withdraw(accountId: ObjectId,
                          withdrawAmount: Double): Mono<BankAccount> {
        if (withdrawAmount < 0) {
            return Mono.error(UnsupportedOperationException("Negative withdraw amount"))
        }

        return findById(accountId).flatMap {
            account ->
                if (account.balance < withdrawAmount)
                    Mono.error(UnsupportedOperationException("Withdraw amount less than account balance"))
                else accountRepository.save(
                    account.copy(balance = account.balance - withdrawAmount)
            )
        }
    }

    override fun transfer(accountFromId: ObjectId,
                          transferAmount: Double,
                          accountToId: ObjectId): Flux<BankAccount> {
        if (transferAmount < 0)
            throw UnsupportedOperationException("Negative transfer amount")

        return Flux.concat(withdraw(accountFromId, transferAmount),
                           deposit(accountToId, transferAmount))
    }

    override fun findById(accountId: ObjectId): Mono<BankAccount> =
        accountRepository.findById(accountId)
            .switchIfEmpty(Mono.error(NoSuchElementException()))
}
