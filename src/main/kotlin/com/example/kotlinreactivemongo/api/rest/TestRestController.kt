package com.example.kotlinreactivemongo.api.rest

import com.example.kotlinreactivemongo.config.security.model.User
import com.example.kotlinreactivemongo.config.security.service.UserReactiveCrudRepository
import org.bson.types.ObjectId
import org.reactivestreams.Publisher
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.web.bind.annotation.RequestMethod.GET


@RestController
@RequestMapping(path = arrayOf("/test"), produces = arrayOf(APPLICATION_JSON_UTF8_VALUE))
class TestRestController(private val userReactiveCrudRepository: UserReactiveCrudRepository) {

    @PostMapping("/create")
    internal fun create(@RequestBody userStream: Publisher<User>): Mono<Void> {
        return this.userReactiveCrudRepository.saveAll(userStream).then()
    }

    @GetMapping("/user/list")
    internal fun list(): Flux<User> {
        return this.userReactiveCrudRepository.findAll()
    }

    @GetMapping("/{id}")
    internal fun findById(@PathVariable id: ObjectId): Mono<User> {
        return this.userReactiveCrudRepository.findById(id)
    }

    @RequestMapping(method = arrayOf(GET), value = "/user/{username}", produces = arrayOf(APPLICATION_JSON_UTF8_VALUE))
    internal fun findById(@PathVariable username: String): Mono<User> {
        return this.userReactiveCrudRepository.findUserByUsername(username)
    }
}