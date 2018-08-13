package com.example.kotlinreactivemongo.config.security.errors

import org.springframework.http.HttpStatus


class UserServiceException(val httpStatus: HttpStatus, override val message: String) : RuntimeException()


