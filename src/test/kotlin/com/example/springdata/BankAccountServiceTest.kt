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
import java.lang.UnsupportedOperationException


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
        var user = userService.createUser(name, email)

        // When
        val bankAccount = bankAccountService.createBankAccount(user.id!!)

        // Then
        assertNotNull(bankAccount)

        user = userService.findByName(name)!!
        assertNotNull(user.bankAccountId)
        assertEquals(0.0, bankAccount.balance)
        assertEquals("default", bankAccount.name)
    }

    @Test
    fun createBankAccountWithName() {
        // Given
        val user = userService.createUser(name, email)

        // When
        val accountName = "deposit account"
        val bankAccount = bankAccountService.createBankAccount(user.id!!, accountName)

        // Then
        assertNotNull(bankAccount)
        assertEquals(accountName, bankAccount.name)
    }

    @Test
    fun createBankAccountNonExistingUser() {
        // Given - id of non-existing user

        // When and Then
        assertThrows(NoSuchElementException::class.java)
        {
            bankAccountService.createBankAccount(nonExistingObjectId)
        }
    }

    @Test
    fun deleteBankAccount() {
        // Given
        var user = userService.createUser(name, email)
        val bankAccount = bankAccountService.createBankAccount(user.id!!)

        // When
        if (bankAccount.id != null) {
            bankAccountService.deleteAccount(bankAccount.id.toString())
        }

        user = userService.findByName(name)!!

        // Then
        assertNull(user.bankAccountId)
        assertEquals(0, bankAccountService.getAll().size)
    }

    @Test
    fun deleteNonExistingBankAccountId() {
        // Given - id of non-existing bank account

        // When and Then
        assertThrows(NoSuchElementException::class.java)
        {
            bankAccountService.deleteAccount(nonExistingObjectId.toString())
        }
    }

    @Test
    fun getAllBankAccounts() {
        // Given
        val user = userService.createUser(name, email)
        bankAccountService.createBankAccount(user.id!!)

        // When
        val accounts = bankAccountService.getAll()

        // Then
        assertEquals(1, accounts.size)
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
    fun depositNonExistingBankAccount() {
        // Given - non-existing account id

        // When and Then
        assertThrows(NoSuchElementException::class.java)
        {
            bankAccountService.deposit(nonExistingObjectId, 100.00)
        }
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
    fun withdrawNegativeAmount() {
        // Given
        val user = userService.createUser(name, email)
        val bankAccount = bankAccountService.createBankAccount(user.id!!)
        bankAccountService.deposit(bankAccount.id!!, 500.0)

        // When and Then
        assertThrows(UnsupportedOperationException::class.java)
        {
            bankAccountService.withdraw(bankAccount.id, -100.00)
        }
    }


    @Test
    fun withdrawMoreThanBalance() {
        // Given
        val user = userService.createUser(name, email)
        var bankAccount = bankAccountService.createBankAccount(user.id!!)
        bankAccount = bankAccountService.deposit(bankAccount.id!!, 500.0)

        // When and Then
        assertThrows(UnsupportedOperationException::class.java)
        {
            bankAccountService.withdraw(bankAccount.id!!, 999.99)
        }
    }

    @Test
    fun transfer() {
        // Given
        val user1 = userService.createUser(name, email)
        var bankAccount1 = bankAccountService.createBankAccount(user1.id!!)
        val user2 = userService.createUser(name2, email2)
        val bankAccount2 = bankAccountService.createBankAccount(user2.id!!)
        bankAccount1 = bankAccountService.deposit(bankAccount1.id!!, 999.99)

        // When
        val accounts: List<BankAccount> = bankAccountService
                        .transfer(bankAccount1.id as ObjectId, 500.00,
                                  bankAccount2.id as ObjectId)

        // Then
        assertEquals(499.99, accounts[0].balance)
        assertEquals(500.00, accounts[1].balance)
    }

    @Test
    fun transferNegativeAmount() {
        // Given
        val user1 = userService.createUser(name, email)
        var bankAccount1 = bankAccountService.createBankAccount(user1.id!!)
        val user2 = userService.createUser(name2, email2)
        val bankAccount2 = bankAccountService.createBankAccount(user2.id!!)
        bankAccount1 = bankAccountService.deposit(bankAccount1.id!!, 999.99)

        // When and Then
        assertThrows(UnsupportedOperationException::class.java)
        { bankAccountService
            .transfer(bankAccount1.id as ObjectId, -100.00,
                bankAccount2.id as ObjectId) }
    }

    @Test
    fun transferMoreThanBalance() {
        // Given
        val user1 = userService.createUser(name, email)
        var bankAccount1 = bankAccountService.createBankAccount(user1.id!!)
        val user2 = userService.createUser(name2, email2)
        val bankAccount2 = bankAccountService.createBankAccount(user2.id!!)
        bankAccount1 = bankAccountService.deposit(bankAccount1.id!!, 999.99)

        // When and Then
        assertThrows(UnsupportedOperationException::class.java)
        { bankAccountService
            .transfer(bankAccount1.id as ObjectId, 1000.00,
                bankAccount2.id as ObjectId) }
    }
}