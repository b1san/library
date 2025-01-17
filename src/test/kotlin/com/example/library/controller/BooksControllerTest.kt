package com.example.library.controller

import com.example.library.exception.CustomResponseStatusException
import com.example.library.exception.ErrorType
import com.example.library.model.authors.AuthorDTO
import com.example.library.model.books.BookDTO
import com.example.library.model.books.BookRequest
import com.example.library.model.error.ErrorDetail
import com.example.library.model.error.ErrorResponse
import com.example.library.service.book.BookService
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDate
import kotlin.test.assertEquals

@WebMvcTest(BookController::class)
class BooksControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var bookService: BookService

    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setup() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    //=================================================================================================================
    // create method
    //=================================================================================================================
    @Test
    fun `should create Book when valid request`() {
        val params = BookRequest("title", 1000, true, emptyList())
        val body = objectMapper.writeValueAsString(params)

        doNothing().`when`(bookService).create(params)

        mockMvc.perform(MockMvcRequestBuilders.post("/api/book").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(""))
    }

    @Test
    fun `should create Book when invalid request`() {
        val params = BookRequest(null, null, null, null)
        val body = objectMapper.writeValueAsString(params)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("title", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("price", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("isPublished", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("authors", "必須入力です。値を入力してください。"))
        val exception = CustomResponseStatusException(ErrorType.INVALID, details)

        doThrow(exception).`when`(bookService).create(params)

        val jsonResponse = mockMvc.perform(MockMvcRequestBuilders.post("/api/book")
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
    fun `should update Book when valid request`() {
        val params = BookRequest("title", 1000, true, emptyList())
        val body = objectMapper.writeValueAsString(params)
        val id = "1"

        doNothing().`when`(bookService).update(id, params)

        mockMvc.perform(MockMvcRequestBuilders.put("/api/book/${id}").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(""))
    }

    @Test
    fun `should update Book when invalid request`() {
        val params = BookRequest(null, null, null, null)
        val body = objectMapper.writeValueAsString(params)
        val id = "1"

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("title", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("price", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("isPublished", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("authors", "必須入力です。値を入力してください。"))
        val exception = CustomResponseStatusException(ErrorType.INVALID, details)

        doThrow(exception).`when`(bookService).update(id, params)

        val jsonResponse = mockMvc.perform(MockMvcRequestBuilders.put("/api/book/${id}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn()
            .response.contentAsString
        val response = objectMapper.readValue(jsonResponse, ErrorResponse::class.java)

        assertEquals(exception.error, response)
    }

    @Test
    fun `should update Book when Book is not found`() {
        val params = BookRequest("title", 1000, true, emptyList())
        val body = objectMapper.writeValueAsString(params)
        val id = "1"

        val exception = CustomResponseStatusException(ErrorType.NO_DATA)

        doThrow(exception).`when`(bookService).update(id, params)

        val jsonResponse = mockMvc.perform(MockMvcRequestBuilders.put("/api/book/${id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andReturn()
            .response.contentAsString
        val response = objectMapper.readValue(jsonResponse, ErrorResponse::class.java)

        assertEquals(exception.error, response)
    }

    //=================================================================================================================
    // find method
    //=================================================================================================================
    @Test
    fun `should get Book when valid request`() {
        val id = "1"

        val result: List<BookDTO> = listOf(
            BookDTO(1, "title1", 1000, true,
                listOf(AuthorDTO(1, "name", LocalDate.now()))),
            BookDTO(2, "title2", 2000, false,
                listOf(AuthorDTO(1, "name", LocalDate.now()),
                    AuthorDTO(2, "name2", LocalDate.of(2020, 1, 1))))
        )

        Mockito.`when`(bookService.findByAuthor(id)).thenReturn(result)

        val jsonResponse = mockMvc.perform(MockMvcRequestBuilders.get("/api/book?authorId=${id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response.contentAsString
        val response = objectMapper.readValue(jsonResponse, object : TypeReference<List<BookDTO>>() {})

        assertEquals(result, response)
    }

    @Test
    fun `should get Book when Book is not found`() {
        val id = "10"
        val exception = CustomResponseStatusException(ErrorType.NO_DATA)
        doThrow(exception).`when`(bookService).findByAuthor(id)

        val jsonResponse = mockMvc.perform(MockMvcRequestBuilders.get("/api/book?authorId=${id}"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andReturn()
            .response.contentAsString
        val response = objectMapper.readValue(jsonResponse, ErrorResponse::class.java)

        assertEquals(exception.error, response)
    }
}