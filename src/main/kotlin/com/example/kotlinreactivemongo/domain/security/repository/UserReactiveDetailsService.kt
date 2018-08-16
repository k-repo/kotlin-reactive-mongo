package com.example.kotlinreactivemongo.domain.security.repository

import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service("userDetailsService")
class UserReactiveDetailsService(private var userRepository: UserReactiveCrudRepository) : ReactiveUserDetailsService {


    override fun findByUsername(username: String): Mono<UserDetails> {
        return userRepository.findByUsername(username)
    }


}