package com.example.kotlinreactivemongo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@SpringBootApplication
@EnableReactiveMongoRepositories
class KotlinReactiveMongoApplication

fun main(args: Array<String>) {
    runApplication<KotlinReactiveMongoApplication>(*args)
}


