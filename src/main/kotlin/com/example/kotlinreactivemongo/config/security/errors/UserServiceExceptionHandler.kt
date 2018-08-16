package com.example.kotlinreactivemongo.config.security.errors

import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class UserServiceExceptionHandler {

    @ExceptionHandler(UserServiceException::class)
    fun handleControllerException(ex: UserServiceException): ResponseEntity<*> {
        return ResponseEntity(ex.message, ex.httpStatus)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: Exception): ResponseEntity<*> {
        return ResponseEntity(ex.message, FORBIDDEN)
    }

    @ExceptionHandler(Exception::class)
    fun handleControllerException(ex: Exception): ResponseEntity<*> {
        return ResponseEntity(ex.message, INTERNAL_SERVER_ERROR)
    }

}