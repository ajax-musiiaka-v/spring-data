package com.example.springdata.grpc_server

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("!test")
@Component
class GrpcServerRunner(val gprcServer: GrpcServer): CommandLineRunner {

    override fun run(vararg args: String?) {
        gprcServer.start()
        gprcServer.blockUntilShutdown()
    }
}
