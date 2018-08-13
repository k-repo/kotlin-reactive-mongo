package com.example.kotlinreactivemongo.config.security.service

import com.example.kotlinreactivemongo.config.security.model.User
import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository


@Repository
interface UserReactiveCrudRepository : ReactiveCrudRepository<User, ObjectId> {
    fun findByUsername(username: String): Mono<UserDetails>
    fun findUserByUsername(username: String): Mono<User>
}