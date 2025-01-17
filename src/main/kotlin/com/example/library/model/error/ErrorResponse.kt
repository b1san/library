package com.example.library.model.error

data class ErrorResponse(var message: String, var details: List<ErrorDetail>?) {
    constructor() : this("", null)
}