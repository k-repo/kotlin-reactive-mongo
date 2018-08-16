package com.example.kotlinreactivemongo.config.security.auth

import com.example.kotlinreactivemongo.config.security.jwt.JwtAuthenticationToken
import com.example.kotlinreactivemongo.config.security.jwt.JwtPreAuthenticationToken
import com.example.kotlinreactivemongo.config.security.jwt.JwtTokenUtil
import com.example.kotlinreactivemongo.domain.security.repository.UserReactiveDetailsService
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.function.Supplier

@Component
class CustomReactiveAuthenticationManager(private val userDetailsService: UserReactiveDetailsService, private val jwtTokenUtil: JwtTokenUtil) : ReactiveAuthenticationManager {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private var passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    init {
        Assert.notNull(userDetailsService, "userDetailsService cannot be null")
        Assert.notNull(jwtTokenUtil, "jwtTokenUtil cannot be null")
    }

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return if (authentication is JwtPreAuthenticationToken) {
            Mono.just<Authentication>(authentication)
                    .switchIfEmpty(Mono.defer(Supplier<Mono<out Authentication>> { this.raiseBadCredentials() }))
                    .cast(JwtPreAuthenticationToken::class.java)
                    .flatMap<UserDetails> { this.authenticateToken(it) }
                    .publishOn(Schedulers.parallel())
                    .onErrorResume { e -> raiseBadCredentials() }
                    .map { u -> JwtAuthenticationToken(u.username, u.authorities) }
        } else Mono.just(authentication)

    }

    private fun <T> raiseBadCredentials(): Mono<T> {
        return Mono.error(BadCredentialsException("Invalid Credentials"))
    }

    private fun authenticateToken(jwtPreAuthenticationToken: JwtPreAuthenticationToken): Mono<UserDetails>? {
        try {
            val authToken = jwtPreAuthenticationToken.authToken
            val username = jwtPreAuthenticationToken.username
            val bearerRequestHeader = jwtPreAuthenticationToken.bearerRequestHeader

            logger.info("checking authentication for user $username")
            if (username != null && SecurityContextHolder.getContext().authentication == null) {
                if (jwtTokenUtil.validateToken(authToken)!!) {
                    logger.info("authenticated user $username, setting security context")
                    val token = authToken
                    return this.userDetailsService.findByUsername(username)
                }
            }
        } catch (e: Exception) {
            throw BadCredentialsException("Invalid token...")
        }

        return null
    }

    fun setPasswordEncoder(passwordEncoder: PasswordEncoder) {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null")
        this.passwordEncoder = passwordEncoder
    }
}