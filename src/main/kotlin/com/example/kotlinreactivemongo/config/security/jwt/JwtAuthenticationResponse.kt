package com.example.kotlinreactivemongo.config.security.jwt

import java.io.Serializable


class JwtAuthenticationResponse(var token: String?,
                                var username: String?,
                                var roles: List<String> = ArrayList()) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}