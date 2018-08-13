package com.example.kotlinreactivemongo.config.security.service

import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.stereotype.Service


@Service("userDetailsService")
class CustomReactiveUserDetailsService : ReactiveUserDetailsService {

    @Autowired
    var userRepository: UserReactiveCrudRepository? = null

    override fun findByUsername(username: String): Mono<UserDetails> {
        return userRepository!!.findByUsername(username)
    }
}