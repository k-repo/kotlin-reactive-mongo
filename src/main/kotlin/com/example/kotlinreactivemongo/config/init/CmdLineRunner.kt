package com.example.kotlinreactivemongo.config.init

import com.example.kotlinreactivemongo.Task
import com.example.kotlinreactivemongo.TaskRepository
import com.example.kotlinreactivemongo.config.security.model.User
import com.example.kotlinreactivemongo.config.security.service.UserReactiveCrudRepository
import org.bson.types.ObjectId
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.util.*

@Component
class CmdLineRunner(private val userReactiveCrudRepository: UserReactiveCrudRepository,
                    private val taskReactiveRepository: TaskRepository) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val people = Flux.just(
                User(ObjectId(),
                        "jdev",
                        "Joe",
                        "Developer",
                        "dev@transempiric.com",
                        "{noop}dev",
                        Arrays.asList("ROLE_ADMIN"),
                        true, Date())
        )


        val task = Flux.just(
                Task(ObjectId(),
                        "Task One",
                        Date(),
                        Date()),
                Task(ObjectId(),
                        "Task Two",
                        Date(),
                        Date())
        )
        taskReactiveRepository.deleteAll().thenMany(taskReactiveRepository.saveAll(task)).blockLast()
        userReactiveCrudRepository.deleteAll().thenMany(userReactiveCrudRepository.saveAll(people)).blockLast()
    }


}
