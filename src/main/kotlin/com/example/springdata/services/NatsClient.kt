package com.example.springdata.services

import com.example.springdata.BankAccountInfoResponse
import com.example.springdata.GetAllBankAccountsRequest
import com.example.springdata.reactive_grpc_service.ReactiveBankAccountGrpcService
import io.nats.client.Connection
import io.nats.client.Nats
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

@Component
class NatsClient(private val reactiveBankAccountGrpcService: ReactiveBankAccountGrpcService) {
    private val connection: Connection = Nats.connect()
    private val subject = "subject"
    private val LOG: Logger = LoggerFactory.getLogger(this::class.java)

    fun run() {
        LOG.info("NATS Started")
        val request = Mono.just(GetAllBankAccountsRequest.newBuilder().build())
        val allAccounts: Flux<BankAccountInfoResponse> = reactiveBankAccountGrpcService.streamAllBankAccounts(request)


        connection.createDispatcher().subscribe(subject) { msg ->
            LOG.info("Received message: [${String(msg.data, StandardCharsets.UTF_8)}] from [${msg.subject}]")
        }

        allAccounts.doOnNext { connection.publish(subject, transformAccount(it)) }.subscribe()
    }

    private fun transformAccount(account: BankAccountInfoResponse): ByteArray =
        ("id=\"${account.bankAccountId}\", " +
                "name=\"${account.bankAccountName}\", " +
                "balance=\"${account.balance}\"").toByteArray()
}