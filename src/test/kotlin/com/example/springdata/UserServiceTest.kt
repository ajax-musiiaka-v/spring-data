package com.example.springdata

import com.example.springdata.entity.User
import com.example.springdata.service.UserService
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import org.bson.Document
import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DuplicateKeyException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier


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
        val userMono: Mono<User> = userService.createUser(name, email)

        //Then
        StepVerifier
            .create(userMono)
            .assertNext { user -> run {
                assertNotNull(user)
                assertNotNull(user.id)
                assertNotNull(user.addressId)
                user.enabled?.let { assertTrue(it) }
            } }
            .verifyComplete()
    }

    @Test
    fun addUserWithNotUniqueEmail() {
        // Given
        val userMono: Mono<User> = userService.createUser(name, email)

        // When and Then
        val userMono2: Mono<User> = userService.createUser(name, email)
        StepVerifier
            .create(userMono)
            .assertNext { user -> assertNotNull(user) }
            .verifyComplete()

        StepVerifier
            .create(userMono2)
            .expectError(DuplicateKeyException::class.java)
            .verify()
    }

    @Test
    fun getAllUsers() {
        // Given
        val userMono = userService.createUser(name, email)
        StepVerifier
            .create(userMono)
            .assertNext { user -> assertNotNull(user) }
            .verifyComplete()

        // When
        val allUsers: Flux<User> = userService.getAll()

        // Then
        StepVerifier
            .create(allUsers)
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun findUserByName() {
        // Given
        userService.createUser(name, email)

        // When
        val userByName = userService.findByName(name)

        // Then
        userByName.subscribe { result ->
            run {
                assertNotNull(result)
                assertEquals(name, result.name)
            }
        }
    }

    @Test
    fun findUserByEmail() {
        // Given
        userService.createUser(name, email)

        // When
        val userByEmail = userService.findByEmail(email)

        // Then
        userByEmail.subscribe { result ->
            run {
                assertNotNull(result)
                assertEquals(email, result.email)
            }
        }
    }

    @Test
    fun deleteUser() {
        // Given
        val userMono = userService.createUser(name, email)
        val userId: String = userMono.block()?.id.toString()

        // When (delete user)
        val deletedUser: Mono<Void> = userService.deleteUser(userId)

        // Then
        StepVerifier.create(deletedUser)
            .verifyComplete()

        val userNotToBeFound: Mono<User> = userService.findById(ObjectId(userId))
        StepVerifier
            .create(userNotToBeFound)
            .expectError(NoSuchElementException::class.java)
            .verify()
    }
}