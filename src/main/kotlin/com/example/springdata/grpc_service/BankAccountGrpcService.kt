package com.example.springdata.grpc_service

import com.example.springdata.*
import com.example.springdata.BankAccountServiceGrpc.BankAccountServiceImplBase
import com.example.springdata.service.BankAccountService
import io.grpc.stub.StreamObserver
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class BankAccountGrpcService(private val bankAccountService: BankAccountService) : BankAccountServiceImplBase() {

    override fun createBankAccount(
        request: CreateBankAccountRequest?,
        responseObserver: StreamObserver<CreateBankAccountResponse>?
    ) {
        val accountCreated: Mono<CreateBankAccountResponse> = bankAccountService
            .createBankAccount(ObjectId(request?.userId), request?.bankAccountName!!)
            .map {
                CreateBankAccountResponse
                    .newBuilder()
                    .setBankAccountId(it.id.toString())
                    .build()
            }

        if (responseObserver != null) {
            accountCreated.subscribe(
                responseObserver::onNext,
                responseObserver::onError,
                responseObserver::onCompleted
            )
        }
    }

    override fun getAllBankAccounts(
        request: GetAllBankAccountsRequest?,
        responseObserver: StreamObserver<GetAllBankAccountsResponse>?
    ) {
        val allAccounts: Flux<BankAccountInfoResponse> = bankAccountService.getAll() // Flux<BankAccount>
            .map {
                BankAccountInfoResponse
                    .newBuilder()
                    .setBankAccountId(it.id.toString())
                    .setBankAccountName(it.bankAccountName)
                    .setBalance(it.balance)
                    .build()
            }

        val response: GetAllBankAccountsResponse = GetAllBankAccountsResponse
            .newBuilder()
            .addAllBankAccounts(allAccounts.toIterable())
            .build()

        if (responseObserver != null) {
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        }
    }

    override fun deleteBankAccount(
        request: DeleteBankAccountRequest?,
        responseObserver: StreamObserver<DeleteBankAccountResponse>?
    ) {
        val deletedMono: Mono<DeleteBankAccountResponse> = bankAccountService
            .deleteAccount(request?.bankAccountId.toString())
            .then(Mono.just(DeleteBankAccountResponse
                .newBuilder()
                .build()))

        if (responseObserver != null) {
            deletedMono.subscribe(
                responseObserver::onNext,
                responseObserver::onError,
                responseObserver::onCompleted
            )
        }
    }
}