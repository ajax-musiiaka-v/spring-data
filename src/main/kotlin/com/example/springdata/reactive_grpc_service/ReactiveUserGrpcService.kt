package com.example.springdata.reactive_grpc_service

import com.example.springdata.*
import com.example.springdata.ReactorUserServiceGrpc.UserServiceImplBase
import com.example.springdata.service.UserService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ReactiveUserGrpcService(private val userService: UserService) : UserServiceImplBase() {

    override fun createUser(request: Mono<CreateUserRequest>?): Mono<CreateUserResponse> {
        val userMono = request?.flatMap { userService.createUser(it.name, it.email) }!!

        return userMono.map {
            CreateUserResponse
                .newBuilder()
                .setUserId(it.id.toString())
                .build()
        }
    }

    override fun getAllUsers(request: Mono<GetAllUsersRequest>?): Mono<GetAllUsersResponse> {
        val allUsers: Flux<UserInfoResponse> = userService.getAll().map {
            UserInfoResponse
                .newBuilder()
                .setUserId(it.id.toString())
                .setName(it.name)
                .setEmail(it.email)
                .setAddressId(it.addressId.toString())
                .setEnabled(it.enabled!!)
                .setBankAccountId(it.bankAccountId.toString())
                .build()
        }
        val listMono: Mono<MutableList<UserInfoResponse>> = allUsers.collectList()

        return listMono
            .map {
                GetAllUsersResponse
                    .newBuilder()
                    .addAllUsers(it)
                    .build()
            }
    }

    override fun deleteUser(request: Mono<DeleteUserRequest>?): Mono<DeleteUserResponse> {
        val deletedMono: Mono<Void> = request?.flatMap {
            userService.deleteUser(it.userId.toString())
        }!!

        return deletedMono.then(Mono.just(
            DeleteUserResponse
                .newBuilder()
                .build()))
    }
}