package com.example.kotlinreactivemongo.api.rest


import com.example.kotlinreactivemongo.config.security.jwt.JwtAuthenticationRequest
import com.example.kotlinreactivemongo.config.security.jwt.JwtAuthenticationResponse
import com.example.kotlinreactivemongo.config.security.jwt.JwtTokenUtil
import com.example.kotlinreactivemongo.config.security.service.UserReactiveCrudRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.RequestMethod.POST

@RestController
@RequestMapping(path = arrayOf("/auth"), produces = arrayOf(APPLICATION_JSON_UTF8_VALUE))
class AuthRestController(private val repo: UserReactiveCrudRepository) {

    @Autowired
    private val jwtTokenUtil: JwtTokenUtil? = null


    @RequestMapping(method = arrayOf(POST), value = "/token")
    @CrossOrigin("*")
    @Throws(AuthenticationException::class)
    fun token(@RequestBody authenticationRequest: JwtAuthenticationRequest): Mono<ResponseEntity<JwtAuthenticationResponse>> {
        val username = authenticationRequest.username
        val password = authenticationRequest.password

        return repo.findByUsername(authenticationRequest.username!!)
                .map { user ->
                    ok().contentType(APPLICATION_JSON_UTF8).body(
                            JwtAuthenticationResponse(jwtTokenUtil!!.generateToken(user), user.username))
                }
                .defaultIfEmpty(notFound().build())
    }
}