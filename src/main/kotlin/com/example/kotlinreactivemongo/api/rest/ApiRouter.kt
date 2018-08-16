package com.example.kotlinreactivemongo.api.rest

import com.example.kotlinreactivemongo.domain.task.model.Task
import com.example.kotlinreactivemongo.domain.task.repository.TaskRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router

@Configuration
class ApiRouter(private val taskRepository: TaskRepository) {
    @Bean
    fun api() = router {
        "/api/rest/tasks".nest {
            GET("/") {
                ServerResponse.ok().body(taskRepository.findAll(), Task::class.java)
            }
            POST("/") {
                ServerResponse.ok().body(taskRepository.insert(it.bodyToMono(Task::class.java)), Task::class.java)
            }
            PUT("/") {
                ServerResponse.ok().body(taskRepository.saveAll(it.bodyToMono(Task::class.java)), Task::class.java)
            }
            DELETE("/{id}") {
                ServerResponse.ok().body(taskRepository.deleteById(it.pathVariable("id")))
            }
        }
    }
}