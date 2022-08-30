package com.example.springdata

import com.example.springdata.entity.BankAccount
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
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.lang.UnsupportedOperationException


@SpringBootTest
@ActiveProfiles("test")
@EnableMongoRepositories(basePackages=["com.example.springdata.repository"])
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
        val userId = createUserGetId()

        // When
        val bankAccountMono = bankAccountService.createBankAccount(userId)

        // Then
        var accountId: ObjectId? = null
        StepVerifier.create(bankAccountMono)
            .assertNext { account ->
                run {
                    assertNotNull(account)
                    assertNotNull(account.id)
                    assertEquals("default", account.bankAccountName)
                    assertEquals(0.0, account.balance)
                    accountId = account.id
                }
            }
            .verifyComplete()

        val userStoredBankAccountId = userService.findById(userId).block()?.bankAccountId
        assertNotNull(userStoredBankAccountId)
        assertEquals(userStoredBankAccountId, accountId)
    }

    @Test
    fun createBankAccountWithName() {
        // Given
        val userId = createUserGetId()

        // When
        val accountName = "deposit account"
        val bankAccountMono = bankAccountService.createBankAccount(userId, accountName)

        // Then
        StepVerifier.create(bankAccountMono)
            .assertNext { account ->
                run {
                    assertNotNull(account)
                    assertNotNull(account.id)
                    assertEquals(accountName, account.bankAccountName)
                    assertEquals(0.0, account.balance)
                }
            }
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
    println()
    }

    @Test
    fun deleteBankAccount() {
        // Given
        val userId = createUserGetId()
        val accountId = createAccountGetId(userId).toString()

        // When
        val deletedMono = bankAccountService.deleteAccount(accountId)

        // Then
        StepVerifier.create(deletedMono)
            .verifyComplete()

        val userStoredBankAccountId = userService.findById(userId).block()?.bankAccountId
        assertNull(userStoredBankAccountId)
    }

//    @Disabled
    @Test
    fun deleteNonExistingBankAccountId() {
        // Given - id of non-existing bank account

        // When
        val deletedMono = bankAccountService.deleteAccount(nonExistingObjectId.toString())

        // Then
        StepVerifier.create(deletedMono)
            .expectError(NoSuchElementException::class.java)
            .verify()
    }

    @Test
    fun getAllBankAccounts() {
        // Given
        val userId = createUserGetId()

        bankAccountService
            .createBankAccount(userId)
            .block()

        // When
        val accounts: Flux<BankAccount> = bankAccountService.getAll()

        // Then
        StepVerifier.create(accounts)
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun deposit() {
        // Given
        val userId = createUserGetId()

        val bankAccountId = createAccountGetId(userId)

        // When
        val deposited = bankAccountService.deposit(bankAccountId, 999.99)

        // Then
        StepVerifier.create(deposited)
            .assertNext { assertEquals(999.99, it.balance) }
            .verifyComplete()
    }

    @Test
    fun withdraw() {
        // Given
        val userId = createUserGetId()

        val bankAccountId = createAccountGetId(userId)

        bankAccountService.deposit(bankAccountId, 999.99).block()

        // When
        val withdrawal  = bankAccountService.withdraw(bankAccountId, 500.0)

        // Then
        StepVerifier.create(withdrawal)
            .assertNext { assertEquals(499.99, it.balance) }
            .verifyComplete()
    }

    @Test
    fun withdrawMoreThanBalance() {
        // Given
        val userId = createUserGetId()
        val bankAccountId = createAccountGetId(userId)

        val accountBalance = 500.0
        bankAccountService.deposit(bankAccountId, accountBalance).block()

        // When
        val withdrawal  = bankAccountService.withdraw(bankAccountId, 999.99)

        // Then
        StepVerifier.create(withdrawal)
            .expectError(UnsupportedOperationException::class.java)
            .verify()

        StepVerifier.create(bankAccountService.findById(bankAccountId))
            .assertNext { assertEquals(accountBalance, it.balance) }
            .verifyComplete()
    }

    @Test
    fun withdrawNegativeAmount() {
        // Given
        val userId = createUserGetId()
        val bankAccountId = createAccountGetId(userId)
        bankAccountService.deposit(bankAccountId, 500.0).block()

        // When
        val withdrawal = bankAccountService.withdraw(bankAccountId, -100.00)

        // Then
        StepVerifier.create(withdrawal)
            .expectError(UnsupportedOperationException::class.java)
            .verify()
    }

    @Test
    fun transfer() {
        // Given
        val userId1 = createUserGetId()
        val bankAccountId1 = createAccountGetId(userId1, "account from")

        val userId2 = createUserGetId(name2, email2)
        val bankAccountId2 = createAccountGetId(userId2, "account to")

        bankAccountService.deposit(bankAccountId1, 999.99).block()

        // When
        val transferred: Flux<BankAccount> = bankAccountService
            .transfer(bankAccountId1, 500.0, bankAccountId2)

        // Then
        StepVerifier.create(transferred)
            .assertNext { assertEquals(499.99, it.balance) }
            .assertNext { assertEquals(500.0, it.balance) }
            .verifyComplete()
    }

    private fun createUserGetId(userName: String = name,
                                userEmail: String = email): ObjectId {
        return userService.createUser(userName, userEmail).block()?.id!!
    }

    private fun createAccountGetId(userId: ObjectId,
                                   accountName:String = "default"): ObjectId {
        return bankAccountService
            .createBankAccount(userId, accountName)
            .block()?.id!!
    }
}
