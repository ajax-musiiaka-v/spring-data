package com.example.springdata.services

import com.example.springdata.BankAccountInfoResponse
import com.example.springdata.entity.BankAccount
import io.nats.client.Connection
import io.nats.client.Message
import io.nats.client.Nats
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

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
            LOG.info("NATS Received message: [${formatForLog(msg)}] from [${msg.subject}]")
        }
    }

    fun bankAccountToProtobuf(account: BankAccount): BankAccountInfoResponse =
        BankAccountInfoResponse
            .newBuilder()
            .setBankAccountId(account.id.toString())
            .setBankAccountName(account.bankAccountName)
            .setBalance(account.balance)
            .setVersion(account.version!!)
            .build()

    fun natsMessageToProtobuf(message: Message): BankAccountInfoResponse =
        BankAccountInfoResponse.parseFrom(message.data)

    private fun formatForLog(message: Message): String =
        natsMessageToProtobuf(message)
            .toString()
            .removeSuffix("\n")
            .replace("\n", ", ")
}
