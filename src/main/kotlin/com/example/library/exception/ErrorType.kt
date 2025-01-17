package com.example.library.exception

import org.springframework.http.HttpStatus

enum class ErrorType(val status: HttpStatus, val message: String) {
    INVALID(HttpStatus.BAD_REQUEST, "バリデーションエラーです。"),
    NO_DATA(HttpStatus.NOT_FOUND, "対象のデータが存在しません。")
}