package com.example.springdata.reactive_grpc_service

import com.example.springdata.*
import com.example.springdata.ReactorBankAccountServiceGrpc.BankAccountServiceImplBase
import com.example.springdata.service.BankAccountService
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ReactiveBankAccountGrpcService(private val bankAccountService: BankAccountService): BankAccountServiceImplBase() {
    override fun createBankAccount(request: Mono<CreateBankAccountRequest>?): Mono<CreateBankAccountResponse> {
        val accountMono = request?.flatMap {
            bankAccountService.createBankAccount(ObjectId(it.userId), it.bankAccountName)
        }!!

        return accountMono.map {
            CreateBankAccountResponse
                .newBuilder()
                .setBankAccountId(it.id.toString())
                .build()
        }
    }

    override fun getAllBankAccounts(request: Mono<GetAllBankAccountsRequest>?): Mono<GetAllBankAccountsResponse> {
        val allAccounts: Flux<BankAccountInfoResponse> = bankAccountService.getAll().map {
            BankAccountInfoResponse
                .newBuilder()
                .setBankAccountId(it.id.toString())
                .setBankAccountName(it.bankAccountName)
                .setBalance(it.balance)
                .build()
        }

        val listMono: Mono<MutableList<BankAccountInfoResponse>> = allAccounts.collectList()

        return listMono
            .map {
                GetAllBankAccountsResponse
                    .newBuilder()
                    .addAllBankAccounts(it)
                    .build()
            }
    }

    override fun deleteBankAccount(request: Mono<DeleteBankAccountRequest>?): Mono<DeleteBankAccountResponse> {
        val deletedMono: Mono<Void> = request?.flatMap {
            bankAccountService.deleteAccount(it.bankAccountId.toString())
        }!!

        return deletedMono.then(Mono.just(
            DeleteBankAccountResponse
                .newBuilder()
                .build()
        ))
    }
}