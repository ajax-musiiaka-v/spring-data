package com.example.springdata.services

import com.example.springdata.BankAccountInfoResponse
import com.example.springdata.entity.BankAccount
import com.google.gson.Gson
import io.nats.client.Connection
import io.nats.client.Message
import io.nats.client.Nats
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class NatsClient {
    val connection: Connection = Nats.connect()
    val allSubjects = "account.>"
    val updateSubject = "account.update"
    val deleteSubject = "account.delete"
    private val LOG: Logger = LoggerFactory.getLogger(this::class.java)

    fun run() {
        LOG.info("NATS Started")

        connection.createDispatcher().subscribe(allSubjects) { msg ->
            LOG.info("NATS Received message: [${String(msg.data, StandardCharsets.UTF_8)}] from [${msg.subject}]")
        }
    }

     fun accountResponseToByteArray(account: BankAccountInfoResponse) =
         Gson().toJson(account).toByteArray()

    fun accountToByteArray(account: BankAccount) =
        account.toString().toByteArray()

    fun natsMessageToBankAccountInfoResponse(message: Message): BankAccountInfoResponse {
        val msgToStr = String(message.data)
        val params = msgToStr.split(", ")

        return BankAccountInfoResponse.newBuilder()
            .setBankAccountId(params[0].split("=")[1])
            .setBankAccountName(params[1].split("=")[1])
            .setBalance(params[2].split("=")[1].toDouble())
            .setVersion(params[3].split("=")[1].dropLast(1).toLong())
            .build()
    }
}
