package com.example.springdata

import com.example.springdata.entity.UserEntity
import com.example.springdata.service.UserService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull


@SpringBootTest
@ActiveProfiles("h2")
class UserServiceTest {

    @Autowired
    var userService: UserService? = null

    @Test
    fun testUserService() {
        assertEquals(0, userService!!.getAll().size)
        val id = userService!!.createUser("john", "doe")
        assertNotNull(id)

        val users: Collection<UserEntity> = userService!!.getAll()
        assertEquals(1, users.size)

        val user: UserEntity? = users.firstOrNull()
        assertNotNull(user)
        assertEquals(id, user?.id)
        assertEquals("john", user?.name)
        assertEquals("doe", user?.email)

        userService!!.deleteUser(id)
        assertEquals(0, userService!!.getAll().size)
    }
}