package com.example.kotlinreactivemongo.config.security.jwt

import java.io.Serializable


class JwtAuthenticationRequest : Serializable {

    var username: String? = null
    var password: String? = null

    constructor() : super() {}

    constructor(username: String, password: String) {
        this.username = username
        this.password = password
    }

    companion object {

        private const val serialVersionUID = 1L
    }
}