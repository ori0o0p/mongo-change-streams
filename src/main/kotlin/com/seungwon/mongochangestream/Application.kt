package com.seungwon.mongochangestream

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux

@EnableWebFlux
@SpringBootApplication
class MongoChangeStreamApplication

fun main(args: Array<String>) {
    runApplication<MongoChangeStreamApplication>(*args)
}
