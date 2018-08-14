package com.example.kotlinreactivemongo.config.security.jwt

import org.springframework.security.authentication.AbstractAuthenticationToken

import javax.security.auth.Subject

class JwtPreAuthenticationToken(val authToken: String,
                                val bearerRequestHeader: String,
                                val username: String) : AbstractAuthenticationToken(null) {

    init {
        isAuthenticated = false
    }

    override fun getCredentials(): Any? {
        return null
    }

    override fun getPrincipal(): Any? {
        return null
    }

    override fun implies(subject: Subject?): Boolean {
        return false
    }
}