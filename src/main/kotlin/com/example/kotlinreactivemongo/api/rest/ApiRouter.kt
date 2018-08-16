package com.example.kotlinreactivemongo.api.rest

import com.example.kotlinreactivemongo.domain.task.model.Task
import com.example.kotlinreactivemongo.domain.task.repository.TaskRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router

@Configuration
class ApiRouter(private val taskRepository: TaskRepository) {
    @Bean
    fun apiTask() = router {
        "/api/rest/tasks".nest {
            GET("/") {
                ok().body(taskRepository
                        .findAll(),
                        Task::class.java)
            }
            GET("/{id}") {
                ok().body(taskRepository.findById(it.pathVariable("id"))
                        , Task::class.java)
            }
            POST("/") {
                ok().body(taskRepository
                        .insert(it
                                .bodyToMono(Task::class.java))
                        , Task::class.java)
            }
            PUT("/") {
                ok().body(taskRepository
                        .saveAll(it
                                .bodyToMono(Task::class.java)),
                        Task::class.java)
            }
            DELETE("/{id}") {
                ok().body(taskRepository
                        .deleteById(it.pathVariable("id")))
            }
        }
    }
}