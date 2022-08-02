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
    fun createUser(@RequestBody request: CreateUserRequest): Mono<ResponseEntity<UserId>> {

        return userService.createUser(request.name, request.email).map { ResponseEntity.ok(getUserId(it)) }
    }

    @GetMapping("/users")
    fun getAll(): Flux<ResponseEntity<UserData>> {

        return userService.getAll().map { ResponseEntity.ok(transform(it)) }
    }

    @DeleteMapping("/users/{id}")
    fun delete(@PathVariable("id") id: String): Mono<ResponseEntity<Void>> {

        return userService.deleteUser(id).map { ResponseEntity.ok().build() }
    }
}

private fun transform(entity: User): UserData =
    UserData(entity.id.toString(), entity.name, entity.email, entity.enabled)

private fun getUserId(entity: User): UserId = UserId(entity.id.toString())


