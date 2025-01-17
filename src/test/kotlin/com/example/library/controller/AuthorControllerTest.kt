package com.example.library.controller

import com.example.library.exception.CustomResponseStatusException
import com.example.library.exception.ErrorType
import com.example.library.model.authors.AuthorRequest
import com.example.library.model.error.ErrorDetail
import com.example.library.model.error.ErrorResponse
import com.example.library.service.author.AuthorService
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import kotlin.test.assertEquals

@WebMvcTest(AuthorController::class)
class AuthorControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var authorService: AuthorService

    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setup() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    //=================================================================================================================
    // create method
    //=================================================================================================================
    @Test
    fun `should create Author when valid request`() {
        val params = AuthorRequest("name", "2000-01-01")
        val body = objectMapper.writeValueAsString(params)

        doNothing().`when`(authorService).create(params)

        mockMvc.perform(MockMvcRequestBuilders.post("/api/author").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(""))
    }

    @Test
    fun `should create Author when invalid request`() {
        val params = AuthorRequest("", "")
        val body = objectMapper.writeValueAsString(params)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("name", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("birthdate", "必須入力です。値を入力してください。"))
        val exception = CustomResponseStatusException(ErrorType.INVALID, details)

        doThrow(exception).`when`(authorService).create(params)

        val jsonResponse = mockMvc.perform(MockMvcRequestBuilders.post("/api/author")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn()
            .response.contentAsString

        val response = objectMapper.readValue(jsonResponse, ErrorResponse::class.java)

        assertEquals(exception.error, response)
    }

    //=================================================================================================================
    // update method
    //=================================================================================================================
    @Test
    fun `should update Author when valid request`() {
        val params = AuthorRequest("name", "2000-01-01")
        val body = objectMapper.writeValueAsString(params)
        val id = "1"

        doNothing().`when`(authorService).update(id, params)

        mockMvc.perform(MockMvcRequestBuilders.put("/api/author/${id}").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(""))
    }

    @Test
    fun `should update Author when invalid request`() {
        val params = AuthorRequest("", "")
        val body = objectMapper.writeValueAsString(params)
        val id = "1"

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("name", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("birthdate", "必須入力です。値を入力してください。"))
        val exception = CustomResponseStatusException(ErrorType.INVALID, details)

        doThrow(exception).`when`(authorService).update(id, params)

        val jsonResponse = mockMvc.perform(MockMvcRequestBuilders.put("/api/author/${id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn()
            .response.contentAsString
        val response = objectMapper.readValue(jsonResponse, ErrorResponse::class.java)

        assertEquals(exception.error, response)
    }

    @Test
    fun `should update Author when Author is not found`() {
        val params = AuthorRequest("name", "2020-01-01")
        val body = objectMapper.writeValueAsString(params)
        val id = "1"

        val exception = CustomResponseStatusException(ErrorType.NO_DATA)

        doThrow(exception).`when`(authorService).update(id, params)

        val jsonResponse = mockMvc.perform(MockMvcRequestBuilders.put("/api/author/${id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andReturn()
            .response.contentAsString
        val response = objectMapper.readValue(jsonResponse, ErrorResponse::class.java)

        assertEquals(exception.error, response)
    }
}