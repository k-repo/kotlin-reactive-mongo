package com.example.kotlinreactivemongo

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.bson.types.ObjectId
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router
import java.util.*

@SpringBootApplication
@EnableReactiveMongoRepositories
class KotlinReactiveMongoApplication

fun main(args: Array<String>) {
    runApplication<KotlinReactiveMongoApplication>(*args)
}

@Document
data class Task(@Id
                @field:JsonSerialize(using = ToStringSerializer::class)
                val id: ObjectId = ObjectId(),
                val name: String,
                val dateBegin: Date,
                val dateEnd: Date)

@Repository
interface TaskRepository : ReactiveMongoRepository<Task,String>


@Configuration
class ApiRouter(private val taskRepository: TaskRepository) {
    @Bean
    fun api() = router {
        "/api/rest/tasks".nest {
            GET("/") {
                ok().body(taskRepository.findAll(), Task::class.java)
            }
            POST("/") {
                ok().body(taskRepository.insert(it.bodyToMono(Task::class.java)), Task::class.java)
            }
            PUT("/") {
                ok().body(taskRepository.saveAll(it.bodyToMono(Task::class.java)), Task::class.java)
            }
            DELETE("/{id}") {
                ok().body(taskRepository.deleteById(it.pathVariable("id")))
            }
        }
    }
}