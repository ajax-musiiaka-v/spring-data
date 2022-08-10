package com.example.springdata.grpc_service

import com.example.springdata.*
import com.example.springdata.UserServiceGrpc.UserServiceImplBase
import com.example.springdata.service.UserService
import io.grpc.stub.StreamObserver
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UserGrpcService(private val userService: UserService): UserServiceImplBase() {

    override fun createUser(request: CreateUserRequest?, responseObserver: StreamObserver<CreateUserResponse>?) {
        val userCreated: Mono<CreateUserResponse> = userService.createUser(request?.name!!, request.email)
            .map {
                CreateUserResponse
                    .newBuilder()
                    .setUserId(it.id.toString())
                    .build()
            }

        if (responseObserver != null) {
            userCreated.subscribe(
                responseObserver::onNext,
                responseObserver::onError,
                responseObserver::onCompleted
            )
        }
    }

    override fun getAllUsers(request: GetAllUsersRequest?,
                             responseObserver: StreamObserver<GetAllUsersResponse>?) {

        val allUsers: Flux<UserInfoResponse> = userService.getAll() // Flux<User>
            .map {
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

            val response: GetAllUsersResponse = GetAllUsersResponse
                .newBuilder()
                .addAllUsers(allUsers.toIterable())
                .build()

        if (responseObserver != null) {
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        }
    }

    override fun deleteUser(request: DeleteUserRequest?,
                            responseObserver: StreamObserver<DeleteUserResponse>?) {

        val deletedMono: Mono<DeleteUserResponse> = userService
            .deleteUser(request?.userId.toString())
            .then(Mono.just(DeleteUserResponse
                .newBuilder()
                .build()))

        if (responseObserver != null) {
            deletedMono.subscribe(
                responseObserver::onNext,
                responseObserver::onError,
                responseObserver::onCompleted
            )
        }
    }

}