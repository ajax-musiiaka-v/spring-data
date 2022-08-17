package com.example.springdata.services

import com.example.springdata.reactive_grpc_service.ReactiveBankAccountGrpcService
import com.example.springdata.reactive_grpc_service.ReactiveUserGrpcService
import io.grpc.Server
import io.grpc.ServerBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GrpcServer(private val reactiveUserGrpcService: ReactiveUserGrpcService,
                 private val reactiveBankAccountGrpcService: ReactiveBankAccountGrpcService
) {

    @Value("\${grpc.port}")
    private lateinit var port: String

    private lateinit var server: Server

    private val LOG: Logger = LoggerFactory.getLogger(this::class.java)

    fun start() {
        server = ServerBuilder
            .forPort(port.toInt())
            .addService(reactiveUserGrpcService)
            .addService(reactiveBankAccountGrpcService)
            .build().start()

        LOG.info("gRPC server started on port: $port.")
        LOG.info("Following Services available:")
        server.services.forEach { LOG.info("Service Name: ${it.serviceDescriptor.name}") }

        Runtime.getRuntime().addShutdownHook(
            Thread {
                LOG.info("Shutting down gRPC server since JVM is shutting down.")
                this@GrpcServer.stop()
                LOG.info("Server shut down.")
            }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }
}
