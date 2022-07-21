package com.example.springdata

import com.example.springdata.entity.User
import com.example.springdata.service.UserService
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import org.bson.Document
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DuplicateKeyException


@SpringBootTest
class UserServiceTest {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var mongoClient: MongoClient

    @Value("\${spring.data.mongodb.database}")
    lateinit var databaseName: String

    val name = "John"
    val email = "doe@mail.com"


    @BeforeEach
    fun setup() {
        mongoClient
            .getDatabase(databaseName)
            .getCollection("users")
            .deleteMany(Filters.eq("name", name))
    }


    @AfterEach
    fun teardown() {
        val db: MongoDatabase = mongoClient.getDatabase(databaseName)
        db.getCollection("users").deleteMany(Document("name", name))
        db.getCollection("users").deleteMany(Document("email", email))
        db.getCollection("addresses").drop()
    }

    @Test
    fun createUser() {
        // Given - empty

        // When
        val user = userService.createUser(name, email)

        //Then
        assertNotNull(user)
    }

    @Test
    fun addUserWithNotUniqueEmail() {
        // Given
        userService.createUser(name, email)

        // When and Then
        assertThrows(DuplicateKeyException::class.java) { userService.createUser(name, email) }
    }

    @Test
    fun getAllUsers() {
        // Given
        userService.createUser(name, email)

        // When
        val users: Collection<User> = userService.getAll()

        // Then
        assertEquals(1, users.size)
    }

    @Test
    fun findUserByName() {
        // Given
        userService.createUser(name, email)

        // When
        val userByName = userService.findByName(name)

        // Then
        assertNotNull(userByName)
        assertEquals(name, userByName?.name)
    }

    @Test
    fun findUserByEmail() {
        // Given
        userService.createUser(name, email)

        // When
        val userByEmail = userService.findByEmail(email)

        // Then
        assertNotNull(userByEmail)
        assertEquals(email, userByEmail?.email)
    }

    @Test
    fun deleteUser() {
        // Given
        userService.createUser(name, email)
        val userFound: User? = userService.getAll().firstOrNull()

        // When
        userService.deleteUser(userFound!!.id.toString())

        // Then
        assertEquals(0, userService.getAll().size)
    }
}