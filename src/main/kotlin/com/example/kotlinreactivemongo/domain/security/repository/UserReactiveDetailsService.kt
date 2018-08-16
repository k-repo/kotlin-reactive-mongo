package com.example.kotlinreactivemongo.domain.security.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service("userDetailsService")
class UserReactiveDetailsService : ReactiveUserDetailsService {

    @Autowired
    var userRepository: UserReactiveCrudRepository? = null

    override fun findByUsername(username: String): Mono<UserDetails> {
        return userRepository!!.findByUsername(username)
    }
}