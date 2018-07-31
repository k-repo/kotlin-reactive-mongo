package com.example.kotlinreactivemongo

import org.bson.types.ObjectId
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router

@SpringBootApplication
class KotlinReactiveMongoApplication

fun main(args: Array<String>) {
    runApplication<KotlinReactiveMongoApplication>(*args)
}

@Document
data class User(@Id val id: String = ObjectId().toString(), val username: String, val password: String,  val email:String)

@Repository
interface UserRepository : ReactiveMongoRepository<User,String>

@Configuration
class ApiRouter(private val userRepository: UserRepository) {
    @Bean
    fun api() = router {
        "/users".nest {
            GET("/") {
              ok().body(userRepository.findAll(), User::class.java)
            }
            POST("/") {
                ok().body(userRepository.insert(it.bodyToMono(User::class.java)), User::class.java)
            }
            PUT("/") {
                ok().body(userRepository.saveAll(it.bodyToMono(User::class.java)), User::class.java)
            }
            DELETE("/{id}") {
                ok().body(userRepository.deleteById(it.pathVariable("id")))
            }
        }
    }
}
