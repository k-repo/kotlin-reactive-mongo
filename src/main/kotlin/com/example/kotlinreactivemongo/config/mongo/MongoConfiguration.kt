package com.example.kotlinreactivemongo.config.mongo

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate


@Configuration
class MongoConfiguration {
    @Bean
    fun reactiveMongoClient(): MongoClient {
        return MongoClients.create("mongodb://localhost")
    }

    @Bean
    fun reactiveMongoTemplate(): ReactiveMongoTemplate {
        return ReactiveMongoTemplate(reactiveMongoClient(), "mydatabase")
    }
}