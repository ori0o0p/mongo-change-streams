package com.example.mongochangestream

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MongoChangeStreamApplication

fun main(args: Array<String>) {
    runApplication<MongoChangeStreamApplication>(*args)
}
