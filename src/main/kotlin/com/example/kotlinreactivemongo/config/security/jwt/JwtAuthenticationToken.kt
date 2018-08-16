package com.example.kotlinreactivemongo.config.security.jwt

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority


internal class JwtAuthenticationToken : UsernamePasswordAuthenticationToken {
    var token: String = ""

    constructor(token: String, username: String, authorities: Collection<GrantedAuthority>) : super(username, null, authorities) {
        this.token = token
    }

    constructor(username: String, authorities: Collection<GrantedAuthority>) : super(username, null, authorities) {}

    override fun getCredentials(): Any? {
        return null
    }
}