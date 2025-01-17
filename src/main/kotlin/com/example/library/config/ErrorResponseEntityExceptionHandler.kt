package com.example.library.config

import com.example.library.exception.CustomResponseStatusException
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ErrorResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(CustomResponseStatusException::class)
    fun handleCustomResponseStatusException(ex: CustomResponseStatusException, request: WebRequest): ResponseEntity<Any>? {
        return handleExceptionInternal(ex, ex.error, HttpHeaders(), ex.status, request)
    }
}