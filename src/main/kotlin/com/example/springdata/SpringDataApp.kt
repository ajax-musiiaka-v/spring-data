package com.example.springdata

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext


@SpringBootApplication
class SpringDataApp {
	private val LOG: Logger = LoggerFactory.getLogger(SpringDataApp::class.java)

	fun startApp(args: Array<String>) {
		LOG.info("Spring Data App started")
		val ctx: ConfigurableApplicationContext = runApplication<SpringDataApp>(*args)
		ctx.registerShutdownHook()
	}
}

fun main(args: Array<String>) {
	SpringDataApp().startApp(args)
}
