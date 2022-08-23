package com.example.springdata

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class SpringDataApp

fun main(args: Array<String>) {
	runApplication<SpringDataApp>(*args)
}
