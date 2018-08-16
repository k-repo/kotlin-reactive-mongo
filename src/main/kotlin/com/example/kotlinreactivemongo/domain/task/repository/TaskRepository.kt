package com.example.kotlinreactivemongo.domain.task.repository

import com.example.kotlinreactivemongo.domain.task.model.Task
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : ReactiveMongoRepository<Task, String>