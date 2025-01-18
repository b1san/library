package com.example.library.config

import com.example.library.exception.CustomResponseStatusException
import com.example.library.model.error.ErrorResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ErrorResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    /**
     * @ResponseBody変換エラー時に発生するHttpMessageNotReadableExceptionのハンドラ
     *
     * @param ex 例外
     * @param headers レスポンスヘッダー
     * @param status HTTPステータス
     * @param request リクエスト
     * @return レスポンス情報
     */
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val body = ErrorResponse("パラメーターの型が一致していません。", null)
        return this.handleExceptionInternal(ex, body, headers, status, request)
    }

    /**
     * 論理エラー用CustomResponseStatusExceptionのハンドラー
     *
     * @param ex 例外
     * @param request リクエスト
     * @return レスポンス情報
     */
    @ExceptionHandler(CustomResponseStatusException::class)
    fun handleCustomResponseStatusException(ex: CustomResponseStatusException, request: WebRequest): ResponseEntity<Any>? {
        return handleExceptionInternal(ex, ex.error, HttpHeaders(), ex.status, request)
    }

    /**
     * ハンドラー未定義例外用のハンドラー
     *
     * @param ex 例外
     * @param request リクエスト
     * @return レスポンス情報
     */
    @ExceptionHandler(Exception::class)
    fun handleAll(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        //TODO スタックトレースのログ出力
        val body = ErrorResponse("予期せぬエラーが発生しました。", null)
        return ResponseEntity<Any>(body, HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}