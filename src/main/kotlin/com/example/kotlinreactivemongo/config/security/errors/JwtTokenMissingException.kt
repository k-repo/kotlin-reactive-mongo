package com.example.kotlinreactivemongo.config.security.errors

import org.springframework.security.core.AuthenticationException

class JwtTokenMissingException(msg: String) : AuthenticationException(msg)