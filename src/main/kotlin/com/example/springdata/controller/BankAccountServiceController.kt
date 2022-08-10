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
class BankAccountServiceController(
    private val bankAccountService: BankAccountService,
    private val userService: UserService
) {

    @PostMapping("/accounts")
    fun createBankAccount(@RequestBody request: CreateBankAccountRequest)
            : Mono<ResponseEntity<BankAccountId>> {

        return bankAccountService
            .createBankAccount(ObjectId(request.userId), request.name)
            .map { ResponseEntity.ok(getAccountId(it)) }
    }

    @GetMapping("/accounts")
    fun getAllBankAccounts(): Flux<ResponseEntity<BankAccountData>> {

        return bankAccountService
            .getAll()
            .map { ResponseEntity.ok(transform(it)) }

    }

    @DeleteMapping("/accounts/{id}")
    fun deleteBankAccount(@PathVariable("id") id: String): Mono<ResponseEntity<Void>> {

        return bankAccountService
            .deleteAccount(id)
            .map { ResponseEntity.ok().build() }
    }
}

private fun transform(entity: BankAccount): BankAccountData =
    BankAccountData(entity.id.toString(), entity.bankAccountName, entity.balance)

private fun getAccountId(entity: BankAccount): BankAccountId =
    BankAccountId(entity.id.toString())
