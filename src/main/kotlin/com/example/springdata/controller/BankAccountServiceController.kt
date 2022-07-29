package com.example.springdata.controller

import com.example.springdata.dto.BankAccountData
import com.example.springdata.dto.BankAccountId
import com.example.springdata.dto.CreateBankAccountRequest
import com.example.springdata.entity.BankAccount
import com.example.springdata.service.BankAccountService
import com.example.springdata.service.UserService
import org.bson.types.ObjectId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/services")
class BankAccountServiceController(private val bankAccountService: BankAccountService,
                                   private val userService: UserService) {

    @PostMapping("/accounts")
    fun createAccount(@RequestBody request: CreateBankAccountRequest)
                                        : ResponseEntity<Mono<BankAccountId>> {

        val bankAccount: Mono<BankAccount> = bankAccountService
            .createBankAccount(ObjectId(request.userId), request.name)
        val accountId: Mono <BankAccountId> = bankAccount.map { getAccountId(it) }

        return ResponseEntity.ok(accountId)
    }

    @GetMapping("/accounts")
    fun getAll(): ResponseEntity<Flux<BankAccountData>> {
        val entities: Flux<BankAccount> = bankAccountService.getAll()
        val bankAccounts: Flux<BankAccountData> = entities.map { transform(it) }

        return ResponseEntity.ok(bankAccounts)

    }

    @DeleteMapping("/accounts/{id}")
    fun delete(@PathVariable("id") id: String): ResponseEntity<Void> {
        bankAccountService.deleteAccount(id)

        return ResponseEntity.ok().build()
    }
}

private fun transform(entity: BankAccount): BankAccountData =
    BankAccountData(entity.id.toString(), entity.name, entity.balance)

private fun getAccountId(entity: BankAccount): BankAccountId =
    BankAccountId(entity.id.toString())