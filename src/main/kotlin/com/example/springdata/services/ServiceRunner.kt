package com.example.springdata.services

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("!test")
@Component
class ServiceRunner(
    val grpcServer: GrpcServer,
    val natsClient: NatsClient) : CommandLineRunner {

    override fun run(vararg args: String?) {
        grpcServer.start()
        natsClient.run()
        grpcServer.blockUntilShutdown()
    }
}
