package com.example.springdata.controller

import com.example.springdata.dto.CreateUserRequest
import com.example.springdata.dto.UserData
import com.example.springdata.dto.UserId
import com.example.springdata.entity.User
import com.example.springdata.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/services")
class UserServiceController(private val userService: UserService) {

    @PostMapping("/users")
    fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<Mono<UserId>> {
        val user: Mono<User> = userService.createUser(request.name, request.email)
        val userId: Mono<UserId> = user.map { getUserId(it) }

        return ResponseEntity.ok(userId)
    }

    @GetMapping("/users")
    fun getAll(): ResponseEntity<Flux<UserData>> {
        val entities: Flux<User> = userService.getAll()
        val users: Flux<UserData> = entities.map { transform(it) }

        return ResponseEntity.ok(users)
    }

    @DeleteMapping("/users/{id}")
    fun delete(@PathVariable("id") id: String): ResponseEntity<Void> {
        userService.deleteUser(id)

        return ResponseEntity.ok().build()
    }
}

private fun transform(entity: User): UserData =
    UserData(entity.id.toString(), entity.name, entity.email, entity.enabled)


private fun getUserId(entity: User): UserId = UserId(entity.id.toString())

