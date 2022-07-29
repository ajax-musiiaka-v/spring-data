package com.example.springdata

import com.example.springdata.entity.User
import com.example.springdata.service.BankAccountService
import com.example.springdata.service.UserService
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import org.bson.Document
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import reactor.core.publisher.Mono
import reactor.test.StepVerifier


@SpringBootTest
class BankAccountServiceTest {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var bankAccountService: BankAccountService

    @Autowired
    lateinit var mongoClient: MongoClient

    @Value("\${spring.data.mongodb.database}")
    lateinit var databaseName: String

    val name = "John"
    val email = "doe@mail.com"

    val name2 = "John2"
    val email2 = "doe2@mail.com"
    // id of non-existing user or non-existing bank account
    val nonExistingObjectId = ObjectId("62d934b3fe3b324cbdb7af51")

    @BeforeEach
    fun setup() {
        val db: MongoDatabase = mongoClient.getDatabase(databaseName)

        db.getCollection("users")
            .deleteMany(Filters.eq("name", name))
        db.getCollection("users")
            .deleteMany(Filters.eq("name", name2))
    }

    @AfterEach
    fun teardown() {
        val db: MongoDatabase = mongoClient.getDatabase(databaseName)
        db.getCollection("users").deleteMany(Document("name", name))
        db.getCollection("users").deleteMany(Document("name", name2))
        db.getCollection("addresses").drop()
        db.getCollection("bank_accounts").drop()
    }

    @Test
    fun createBankAccount() {
        // Given
        val userMono: Mono<User> = userService.createUser(name, email)

        val userId: ObjectId = userMono.block()?.id as ObjectId

        // When
        val bankAccountMono = bankAccountService.createBankAccount(userId)

        // Then
        StepVerifier.create(bankAccountMono)
            .assertNext { account -> run {
                assertNotNull(account)
                assertNotNull(account.id)
                assertEquals("default", account.name)
                assertEquals(0.0, account.balance)
            } }
            .verifyComplete()
    }

    @Test
    fun createBankAccountWithName() {
        // Given
        val userMono: Mono<User> = userService.createUser(name, email)

        val userId: ObjectId = userMono.block()?.id as ObjectId

        // When
        val accountName = "deposit account"
        val bankAccountMono = bankAccountService.createBankAccount(userId, accountName)

        // Then
        StepVerifier.create(bankAccountMono)
            .assertNext { account -> run {
                assertNotNull(account)
                assertNotNull(account.id)
                assertEquals(accountName, account.name)
                assertEquals(0.0, account.balance)
            } }
            .verifyComplete()
    }

    @Disabled
    @Test
    fun createBankAccountNonExistingUser() {
        // Given - id of non-existing user

        // When
        val bankAccountMono = bankAccountService.createBankAccount(nonExistingObjectId)

        // Then
        StepVerifier.create(bankAccountMono)
            .expectError(NoSuchElementException::class.java)
            .verify()

    }

    @Test
    fun deleteBankAccount() {
        // Given
        val userMono = userService.createUser(name, email)
        val userId: ObjectId = userMono.block()?.id as ObjectId
        val bankAccountMono = bankAccountService.createBankAccount(userId)
        val accountId = bankAccountMono.block()?.id.toString()

        // When
        val deletedMono = bankAccountService.deleteAccount(accountId)

        // Then
        StepVerifier.create(deletedMono)
            .verifyComplete()
    }

    @Disabled
    @Test
    fun deleteNonExistingBankAccountId() {
        // Given - id of non-existing bank account

        // When and Then
        assertThrows(NoSuchElementException::class.java)
        {
            bankAccountService.deleteAccount(nonExistingObjectId.toString())
        }
    }
}