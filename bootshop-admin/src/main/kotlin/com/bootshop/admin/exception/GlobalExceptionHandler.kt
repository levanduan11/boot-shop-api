package com.bootshop.admin.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(value = [BadCredentialsException::class])
    fun onBadCredential(ex:BadCredentialsException):ResponseEntity<Any>{
        val body = mapOf(
            "message" to ex.message
        )
        return ResponseEntity(body,HttpStatus.FORBIDDEN)

    }
    @ExceptionHandler(value = [Exception::class])
    fun onUnknownException(ex:Exception):ResponseEntity<Any>{
        return ResponseEntity.internalServerError().body(ex)
    }
}