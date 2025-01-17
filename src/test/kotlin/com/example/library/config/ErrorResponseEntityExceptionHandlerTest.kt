package com.example.library.config

import com.example.library.exception.CustomResponseStatusException
import com.example.library.exception.ErrorType
import com.example.library.model.error.ErrorDetail
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.context.request.ServletWebRequest


class ErrorResponseEntityExceptionHandlerTest {

    private val handler = ErrorResponseEntityExceptionHandler()
    private val mockRequest = MockHttpServletRequest().apply {
        requestURI = "/test"
        method = "GET"
    }
    private val mockResponse = MockHttpServletResponse()
    private val webRequest = ServletWebRequest(mockRequest, mockResponse)

    @Test
    fun `CustomResponseStatusException to return the correct response`() {
        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("name", "name is null or empty."))
        details.add(ErrorDetail("birthdate", "birthdate is null or empty"))

        val exception = CustomResponseStatusException(ErrorType.INVALID, details)

        val response = handler.handleCustomResponseStatusException(exception, webRequest)

        assertEquals(ErrorType.INVALID.status.value(), response?.statusCode?.value())
        assertEquals(exception.error, response?.body)
    }
}