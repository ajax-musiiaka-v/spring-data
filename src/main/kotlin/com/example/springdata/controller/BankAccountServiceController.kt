package com.example.springdata.controller

import com.example.springdata.dto.BankAccountData
import com.example.springdata.dto.BankAccountId
import com.example.springdata.dto.CreateBankAccountRequest
import com.example.springdata.entity.BankAccount
import com.example.springdata.service.BankAccountService
import org.bson.types.ObjectId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/services")
class BankAccountServiceController(private val bankAccountService: BankAccountService) {

    @PostMapping("/accounts")
    fun createAccount(@RequestBody request: CreateBankAccountRequest)
                                        : ResponseEntity<BankAccountId> {

        val id: String = bankAccountService
            .createBankAccount(ObjectId(request.userId), request.name).id.toString()

        return ResponseEntity.ok(BankAccountId(id))
    }

    @GetMapping("/accounts")
    fun getAll(): ResponseEntity<Collection<BankAccountData>> {
        val entities = bankAccountService.getAll()
        val bankAccounts: Collection<BankAccountData> = entities.map { transform(it) }

        return ResponseEntity.ok(bankAccounts)

    }

    @DeleteMapping("/accounts/{id}")
    fun delete(@PathVariable("id") id: String): ResponseEntity<Void> {
        bankAccountService.deleteAccount(id)

        return ResponseEntity.ok().build()
    }
}

private fun transform(entity: BankAccount): BankAccountData {
    return BankAccountData(entity.id.toString(), entity.name, entity.balance)
}