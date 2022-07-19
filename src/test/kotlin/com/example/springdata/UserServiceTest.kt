package com.example.springdata

import com.example.springdata.entity.User
import com.example.springdata.service.UserService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull


@SpringBootTest
class UserServiceTest {

    @Autowired
    var userService: UserService? = null

    @Test
    fun testUserService() {
        val name = "John"
        val email = "doe@mail.com"

        assertEquals(0, userService!!.getAll().size)
        val user = userService!!.createUser(name, email)
        assertNotNull(user)

        val users: Collection<User> = userService!!.getAll()
        assertEquals(1, users.size)

        val userFound: User? = users.firstOrNull()
        assertNotNull(userFound)

        assertEquals(name, userFound?.name)
        assertEquals(email, userFound?.email)

        val userByName = userService!!.findByName(name)
        assertNotNull(userByName)
        assertEquals(name, userByName?.name)

        val userByEmail = userService!!.findByEmail(email)
        assertNotNull(userByEmail)
        assertEquals(email, userByEmail?.email)

        userService!!.deleteUser(userFound!!.id.toString())
        assertEquals(0, userService!!.getAll().size)
    }
}