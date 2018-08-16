package com.example.kotlinreactivemongo.domain.security.repository

import com.example.kotlinreactivemongo.domain.security.model.User
import org.bson.types.ObjectId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono


@Repository
interface UserReactiveCrudRepository : ReactiveCrudRepository<User, ObjectId> {
    fun findByUsername(username: String): Mono<UserDetails>
    fun findByUsernameAndPassword(username: String, password: String): Mono<User>
}