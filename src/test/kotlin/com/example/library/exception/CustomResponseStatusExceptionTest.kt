package com.example.library.exception

import com.example.library.model.error.ErrorDetail
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CustomResponseStatusExceptionTest {

    @Test
    fun `define the Exception with details`() {
        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("name", "name is null or empty."))
        details.add(ErrorDetail("birthdate", "birthdate is null or empty"))

        val exception = CustomResponseStatusException(ErrorType.INVALID, details)

        assertEquals(exception.status, ErrorType.INVALID.status)
        assertEquals(exception.error.message, ErrorType.INVALID.message)
        assertEquals(exception.error.details, details)
    }

    @Test
    fun `define the Exception with Optional details`() {
        val exception = CustomResponseStatusException(ErrorType.NO_DATA)

        assertEquals(exception.status, ErrorType.NO_DATA.status)
        assertEquals(exception.error.message, ErrorType.NO_DATA.message)
        assertEquals(exception.error.details, null)
    }
}