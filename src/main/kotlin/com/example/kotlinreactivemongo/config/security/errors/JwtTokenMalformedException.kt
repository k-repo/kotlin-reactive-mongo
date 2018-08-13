package com.example.kotlinreactivemongo.config.security.errors

import org.springframework.security.core.AuthenticationException

class JwtTokenMalformedException : AuthenticationException {
    constructor(msg: String) : super(msg)
    constructor(msg: String, throwable: Throwable) : super(msg, throwable)
}