package com.example.kotlinreactivemongo.config.security.jwt

import com.example.kotlinreactivemongo.config.security.service.CustomReactiveUserDetailsService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.function.Function

@Component
class JwtAuthenticationConverter(private val userDetailService: CustomReactiveUserDetailsService,
                                 private val jwtTokenUtil: JwtTokenUtil) : Function<ServerWebExchange, Mono<Authentication>> {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jwt.header}")
    private val tokenHeader: String? = null

    @Value("\${jwt.param}")
    private val tokenParam: String? = null

    @Value("\${jwt.prefix}")
    private val bearerPrefix: String? = null

    @Throws(BadCredentialsException::class)
    override fun apply(exchange: ServerWebExchange): Mono<Authentication> {
        val request = exchange.request
        try {

            val authentication: Authentication? = null
            var authToken: String? = null
            var username: String? = null

            val bearerRequestHeader = exchange.request.headers.getFirst(tokenHeader.toString())

            if (bearerRequestHeader != null && bearerRequestHeader.startsWith(bearerPrefix!! + " ")) {
                authToken = bearerRequestHeader.substring(7)
            }

            if (authToken == null && request.queryParams != null && !request.queryParams.isEmpty()) {
                val authTokenParam = request.queryParams.getFirst(tokenParam.toString())
                if (authTokenParam != null) authToken = authTokenParam
            }

            if (authToken != null) {
                try {
                    username = jwtTokenUtil.getUsernameFromToken(authToken)
                } catch (e: IllegalArgumentException) {
                    logger.error("an error occured during getting username from token", e)
                } catch (e: Exception) {
                    logger.warn("the token is expired and not valid anymore", e)
                }

            } else {
                logger.warn("couldn't find bearer string, will ignore the header")
            }

            logger.info("checking authentication for user " + username!!)
            return if (username != null && SecurityContextHolder.getContext().authentication == null) {

                return Mono.just(JwtPreAuthenticationToken(authToken!!, bearerRequestHeader!!, username))
            } else Mono.just(authentication!!)

        } catch (e: Exception) {
            throw BadCredentialsException("Invalid token...")
        }

    }
}