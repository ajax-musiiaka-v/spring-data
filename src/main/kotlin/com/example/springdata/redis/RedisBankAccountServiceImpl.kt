package com.example.springdata.redis

import com.example.springdata.entity.BankAccount
import com.example.springdata.service.BankAccountService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveHashOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class RedisBankAccountServiceImpl(
    private val redisTemplate: ReactiveRedisTemplate<String, BankAccount>,
    private val bankAccountService: BankAccountService
)
    : RedisBankAccountService {

    private val hashOps: ReactiveHashOperations<String, String, BankAccount> = redisTemplate.opsForHash()

    @Value("\${spring.data.redis.bank_accounts_db}")
    private lateinit var KEY: String

    override fun findById(accountId: ObjectId): Mono<BankAccount> {

        val accountMono = hashOps.get(KEY, accountId.toString())
            .switchIfEmpty(bankAccountService.findById(accountId)
                .flatMap { hashOps.put(KEY, accountId.toString(), it)
                    .then(Mono.just(it)) })

        return accountMono
    }

    override fun getAll(): Flux<BankAccount> {
        val accounts = bankAccountService.getAll()
                .flatMap { hashOps.put(KEY, it.id.toString(), it)
                    .then(Mono.just(it)) }

        return accounts
    }

    override fun deleteAccount(bankAccountId: String): Mono<Void> {
        return hashOps.remove(KEY, bankAccountId)
            .then(bankAccountService.deleteAccount(bankAccountId))
    }

    override fun deposit(accountId: ObjectId, depositAmount: Double): Mono<BankAccount> {
        if (depositAmount < 0) {
            return Mono.error(UnsupportedOperationException("Negative deposit amount"))
        }
        val accountMono = bankAccountService.deposit(accountId, depositAmount)
                .flatMap { hashOps.put(KEY, accountId.toString(), it).then(Mono.just(it)) }

        return accountMono
    }
}
