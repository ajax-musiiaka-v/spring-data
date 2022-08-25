package com.example.springdata.reactive_grpc_service

import com.example.springdata.*
import com.example.springdata.ReactorBankAccountServiceGrpc.BankAccountServiceImplBase
import com.example.springdata.redis.RedisBankAccountServiceImpl
import com.example.springdata.service.BankAccountService
import com.example.springdata.services.NatsClient
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ReactiveBankAccountGrpcService(private val bankAccountService: BankAccountService,
                                     private val natsClient: NatsClient,
                                     private val redisCacheService: RedisBankAccountServiceImpl

                                     ): BankAccountServiceImplBase() {
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

    override fun findBankAccountById(request: Mono<BankAccountInfoRequest>?): Mono<BankAccountInfoResponse> {
        val accountMono = request?.flatMap {
            redisCacheService
                .findById(ObjectId(it.bankAccountId)) }!!


        return accountMono.map {
            natsClient.bankAccountToProtobuf(it)
        }
    }

    override fun getAllBankAccounts(request: Mono<GetAllBankAccountsRequest>?): Mono<GetAllBankAccountsResponse> {
        val allAccounts: Flux<BankAccountInfoResponse> =
            redisCacheService
                .getAll().map {
            natsClient.bankAccountToProtobuf(it)
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

    override fun streamAllBankAccounts(request: Mono<GetAllBankAccountsRequest>?): Flux<BankAccountInfoResponse> {
        val dbResponse = bankAccountService.getAll().map {
            natsClient.bankAccountToProtobuf(it)
        }

        val natsResponse = Flux.create<BankAccountInfoResponse> { sink ->
            run {
                natsClient.connection.createDispatcher { message -> sink.next(natsClient.natsMessageToProtobuf(message)) }
                    .subscribe(natsClient.updateSubject)
            }
        }

        return Flux.merge(dbResponse, natsResponse)
    }

    override fun deleteBankAccount(request: Mono<DeleteBankAccountRequest>?): Mono<DeleteBankAccountResponse> {
        val deletedMono: Mono<Void> = request?.flatMap {
            redisCacheService
                .deleteAccount(it.bankAccountId.toString())
        }!!

        return deletedMono.then(Mono.just(
            DeleteBankAccountResponse
                .newBuilder()
                .build()
        ))
    }

    override fun depositBankAccount(request: Mono<DepositBankAccountRequest>?): Mono<BankAccountInfoResponse> {
        val accountMono = request?.flatMap {
            redisCacheService
                .deposit(ObjectId(it.bankAccountId), it.amount)
        }!!
        return accountMono.map {
            natsClient.bankAccountToProtobuf(it)
        }
    }
}

