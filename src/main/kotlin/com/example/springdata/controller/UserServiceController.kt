package com.example.springdata.controller

import com.example.springdata.dto.CreateUserRequest
import com.example.springdata.dto.UserData
import com.example.springdata.dto.UserId
import com.example.springdata.entity.User
import com.example.springdata.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/services")
class UserServiceController(private val userService: UserService) {

    @PostMapping("/users")
    fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<UserId> {
        val id: String = userService.createUser(request.name, request.email).id.toString()

        return ResponseEntity.ok(UserId(id))
    }

    @GetMapping("/users")
    fun getAll(): ResponseEntity<Collection<UserData>> {
        val entities = userService.getAll()
        val users: Collection<UserData> = entities.map { transform(it) }

        return ResponseEntity.ok(users)
    }

    @DeleteMapping("/users/{id}")
    fun delete(@PathVariable("id") id: String): ResponseEntity<Void> {
        userService.deleteUser(id)

        return ResponseEntity.ok().build()
    }
}

private fun transform(entity: User): UserData {
    return UserData(entity.id.toString(), entity.name, entity.email, entity.enabled)
}

