package com.example.springdata.service

import com.example.springdata.entity.BankAccount
import com.example.springdata.repository.BankAccountRepository
import com.example.springdata.services.NatsClient
import org.bson.types.ObjectId
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class BankAccountServiceImpl(private val accountRepository: BankAccountRepository,
                             private val userService: UserService,
                             private val natsClient: NatsClient
                             ) : BankAccountService {

    override fun getAll(): Flux<BankAccount> =
        accountRepository.findAll()

    override fun createBankAccount(userId: ObjectId, accountName: String): Mono<BankAccount> {

        return accountRepository
            .save(BankAccount(bankAccountName = accountName))
            .zipWith(userService.findById(userId))
            .flatMap {      // t1 - bankAccount, t2 - user
                userService
                    .updateUser(it.t2.copy(bankAccountId = it.t1.id))
                    .then(Mono.just(it.t1))
            }
            .publishUpdateToNats()
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
            .doOnSuccess {
                natsClient.connection
                    .publish(natsClient.deleteSubject, "id=\"$bankAccountId\" deleted".toByteArray())
            }
    }

    override fun deposit(accountId: ObjectId,
                         depositAmount: Double): Mono<BankAccount> {
        if (depositAmount < 0) {
            return Mono.error(UnsupportedOperationException("Negative deposit amount"))
        }

        return findById(accountId).flatMap {
            accountRepository.save(
                it.copy(balance = it.balance + depositAmount)
            ) }
            .publishUpdateToNats()
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
            ) }
            .publishUpdateToNats()
    }

    override fun transfer(accountFromId: ObjectId,
                          transferAmount: Double,
                          accountToId: ObjectId): Flux<BankAccount> {
        if (transferAmount < 0)
            throw UnsupportedOperationException("Negative transfer amount")

        return Flux.concat(withdraw(accountFromId, transferAmount),
                           deposit(accountToId, transferAmount))
        // publishing to NATS is done for each account separately from withdraw / deposit method
    }

    override fun findById(accountId: ObjectId): Mono<BankAccount> =
        accountRepository.findById(accountId)
            .switchIfEmpty(Mono.error(NoSuchElementException()))

    private fun Mono<BankAccount>.publishUpdateToNats(): Mono<BankAccount> {
        return this.doOnSuccess {
            natsClient.connection
                .publish(natsClient.updateSubject, natsClient.bankAccountToProtobuf(it).toByteArray())
        }
    }
}
