package com.example.library.exception

import com.example.library.model.error.ErrorDetail
import com.example.library.model.error.ErrorResponse

class CustomResponseStatusException(private val type: ErrorType, private val details: List<ErrorDetail>?) : RuntimeException() {
    val error = ErrorResponse(type.message, details)
    var status = type.status

    constructor(type: ErrorType) : this(type, null)
}