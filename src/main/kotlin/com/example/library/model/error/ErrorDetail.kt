package com.example.library.model.error

data class ErrorDetail(var field: String, var message: String) {
    constructor(): this("", "")
}
