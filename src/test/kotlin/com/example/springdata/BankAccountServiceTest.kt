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

    @BeforeEach
    fun setup() {
        val db: MongoDatabase = mongoClient.getDatabase(databaseName)

        db.getCollection("users")
            .deleteMany(Filters.eq("name", name))
        db.getCollection("users")
            .deleteMany(Filters.eq("name", "name2"))
    }

    @AfterEach
    fun teardown() {
        val db: MongoDatabase = mongoClient.getDatabase(databaseName)
        db.getCollection("users").deleteMany(Document("name", name))
        db.getCollection("users").deleteMany(Document("name", "name2"))
        db.getCollection("users").deleteMany(Document("email", email))
        db.getCollection("addresses").drop()
        db.getCollection("bank_accounts").drop()
    }

    @Test
    fun createBankAccount() {
        // Given
        var user = userService.createUser(name, email)

        // When
        val bankAccount = bankAccountService.createBankAccount(user.id!!)

        // Then
        assertNotNull(bankAccount)
        assertEquals(1, bankAccountService.getAll().size)

        user = userService.findByName(name)!!
        assertNotNull(user.bankAccountId)
        assertEquals(0.0, bankAccount.balance)
    }

    @Test
    fun deleteBankAccount() {
        // Given
        var user = userService.createUser(name, email)
        val bankAccount = bankAccountService.createBankAccount(user.id!!)

        // When
        if (bankAccount.id != null) {
            bankAccountService.deleteAccount(bankAccount.id)
        }

        user = userService.findByName(name)!!

        // Then
        assertNull(user.bankAccountId)
        assertEquals(0, bankAccountService.getAll().size)
    }

    @Test
    fun deposit() {
        // Given
        val user = userService.createUser(name, email)
        var bankAccount = bankAccountService.createBankAccount(user.id!!)

        // When
        bankAccount = bankAccountService.deposit(bankAccount.id as ObjectId, 999.99)

        // Then
        assertEquals(999.99, bankAccount.balance)
    }

    @Test
    fun withdraw() {
        // Given
        val user = userService.createUser(name, email)
        var bankAccount = bankAccountService.createBankAccount(user.id!!)
        bankAccount = bankAccountService.deposit(bankAccount.id as ObjectId, 999.99)

        // When
        bankAccount = bankAccountService.withdraw(bankAccount.id as ObjectId, 500.00)

        // Then
        assertEquals(499.99, bankAccount.balance)
    }

    @Test
    fun transfer() {
        // Given
        val user1 = userService.createUser(name, email)
        var bankAccount1 = bankAccountService.createBankAccount(user1.id!!)
        val user2 = userService.createUser("name2", "email2@mail.com")
        val bankAccount2 = bankAccountService.createBankAccount(user2.id!!)
        bankAccount1 = bankAccountService.deposit(bankAccount1.id as ObjectId, 999.99)

        // When
        val accounts: List<BankAccount> = bankAccountService
                        .transfer(bankAccount1.id as ObjectId, 500.00,
                                  bankAccount2.id as ObjectId)

        // Then
        assertEquals(499.99, accounts[0].balance)
        assertEquals(500.00, accounts[1].balance)

    }
}