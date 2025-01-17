package com.example.library.service

import com.example.library.exception.CustomResponseStatusException
import com.example.library.model.authors.AuthorDTO
import com.example.library.model.authors.AuthorRequest
import com.example.library.model.error.ErrorDetail
import com.example.library.repository.author.AuthorRepository
import com.example.library.service.author.AuthorServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doNothing
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

@SpringBootTest
class AuthorServiceTest {

    @Mock
    lateinit var authorRepository: AuthorRepository

    @InjectMocks
    lateinit var authorService: AuthorServiceImpl

    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    lateinit var params: AuthorRequest
    lateinit var dto: AuthorDTO

    @BeforeEach
    fun setup() {
        val name = "a".repeat(255)
        val birthdate = LocalDate.now().minusDays(1)
        params = AuthorRequest(name, birthdate.format(formatter))
        dto = AuthorDTO(1, name, birthdate)
    }

    //=================================================================================================================
    // create method
    //=================================================================================================================
    @Test
    fun `should create Author when valid params are provided`() {
        Mockito.`when`(authorRepository.create(dto)).thenReturn(1)
        assertDoesNotThrow {
            authorService.create(params)
        }
    }

    @Test
    fun `should throw exception when creating with null params`() {
        val invalidParams = params.copy(name = null, birthdate = null)

        Mockito.`when`(authorRepository.create(dto)).thenReturn(1)

        val exception = assertThrows<CustomResponseStatusException> {
            authorService.create(invalidParams)
        }
        assertEquals(exception.status, HttpStatus.BAD_REQUEST)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("name", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("birthdate", "必須入力です。値を入力してください。"))

        assertEquals(details, exception.error.details)
    }

    @Test
    fun `should throw exception when creating with empty params`() {
        val invalidParams = params.copy(name = "", birthdate = "")

        Mockito.`when`(authorRepository.create(dto)).thenReturn(1)

        val exception = assertThrows<CustomResponseStatusException> {
            authorService.create(invalidParams)
        }
        assertEquals(exception.status, HttpStatus.BAD_REQUEST)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("name", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("birthdate", "必須入力です。値を入力してください。"))

        assertEquals(details, exception.error.details)
    }

    @Test
    fun `should throw exception when creating with character limit`() {
        val invalidParams = params.copy(name = "a".repeat(256))

        Mockito.`when`(authorRepository.create(dto)).thenReturn(1)

        val exception = assertThrows<CustomResponseStatusException> {
            authorService.create(invalidParams)
        }
        assertEquals(exception.status, HttpStatus.BAD_REQUEST)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("name", "最大文字数を超えました。255文字以内で入力してください。"))

        assertEquals(details, exception.error.details)
    }

    @Test
    fun `should throw exception when creating with a birthdate that is the current date`() {
        val invalidParams = params.copy(birthdate = LocalDate.now().format(formatter))

        Mockito.`when`(authorRepository.create(dto)).thenReturn(1)

        val exception = assertThrows<CustomResponseStatusException> {
            authorService.create(invalidParams)
        }
        assertEquals(exception.status, HttpStatus.BAD_REQUEST)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("birthdate", "現在日より過去の日付を入力してください。"))

        assertEquals(details, exception.error.details)
    }

    @Test
    fun `should throw exception when creating with a birthdate that is the future date`() {
        val invalidParams = params.copy(birthdate = LocalDate.now().plusDays(1).format(formatter))

        Mockito.`when`(authorRepository.create(dto)).thenReturn(1)

        val exception = assertThrows<CustomResponseStatusException> {
            authorService.create(invalidParams)
        }
        assertEquals(exception.status, HttpStatus.BAD_REQUEST)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("birthdate", "現在日より過去の日付を入力してください。"))

        assertEquals(details, exception.error.details)
    }

    @Test
    fun `should throw exception when creating with an invalid date format`() {
        val invalidParams = params.copy(birthdate = "20200101")

        Mockito.`when`(authorRepository.create(dto)).thenReturn(1)

        val exception = assertThrows<CustomResponseStatusException> {
            authorService.create(invalidParams)
        }
        assertEquals(exception.status, HttpStatus.BAD_REQUEST)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("birthdate", "正しい日付を入力してください（YYYY-MM-DD）。"))

        assertEquals(details, exception.error.details)
    }

    @Test
    fun `should throw exception when creating with an invalid date range`() {
        val invalidParams = params.copy(birthdate = "2020-04-31")

        Mockito.`when`(authorRepository.create(dto)).thenReturn(1)

        val exception = assertThrows<CustomResponseStatusException> {
            authorService.create(invalidParams)
        }
        assertEquals(exception.status, HttpStatus.BAD_REQUEST)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("birthdate", "正しい日付を入力してください（YYYY-MM-DD）。"))

        assertEquals(details, exception.error.details)
    }

    //=================================================================================================================
    // update method
    //=================================================================================================================
    @Test
    fun `should update Author when valid params are provided`() {
        doNothing().`when`(authorRepository).update(dto)
        Mockito.`when`(authorRepository.existsById(1)).thenReturn(true)

        assertDoesNotThrow {
            authorService.update("1", params)
        }
    }

    @Test
    fun `should throw exception when updating with an invalid id`() {
        doNothing().`when`(authorRepository).update(dto)
        Mockito.`when`(authorRepository.existsById(0)).thenReturn(true)

        val exception = assertThrows<CustomResponseStatusException> {
            authorService.update("0", params)
        }
        assertEquals(HttpStatus.NOT_FOUND, exception.status)
    }

    @Test
    fun `should throw exception when updating with not exists data`() {
        doNothing().`when`(authorRepository).update(dto)
        Mockito.`when`(authorRepository.existsById(1)).thenReturn(false)

        val exception = assertThrows<CustomResponseStatusException> {
            authorService.update("1", params)
        }
        assertEquals(HttpStatus.NOT_FOUND, exception.status)
    }

    @Test
    fun `should throw exception when updating with null params`() {
        val invalidParams = params.copy(name = null, birthdate = null)

        doNothing().`when`(authorRepository).update(dto)
        Mockito.`when`(authorRepository.existsById(1)).thenReturn(true)

        val exception = assertThrows<CustomResponseStatusException> {
            authorService.update("1", invalidParams)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("name", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("birthdate", "必須入力です。値を入力してください。"))

        assertEquals(details, exception.error.details)
    }

    @Test
    fun `should throw exception when updating with empty params`() {
        val invalidParams = params.copy(name = "", birthdate = "")

        doNothing().`when`(authorRepository).update(dto)
        Mockito.`when`(authorRepository.existsById(1)).thenReturn(true)

        val exception = assertThrows<CustomResponseStatusException> {
            authorService.update("1", invalidParams)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("name", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("birthdate", "必須入力です。値を入力してください。"))

        assertEquals(details, exception.error.details)
    }

    @Test
    fun `should throw exception when updating with character limit`() {
        val invalidParams = params.copy(name = "a".repeat(256))

        doNothing().`when`(authorRepository).update(dto)
        Mockito.`when`(authorRepository.existsById(1)).thenReturn(true)

        val exception = assertThrows<CustomResponseStatusException> {
            authorService.update("1", invalidParams)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("name", "最大文字数を超えました。255文字以内で入力してください。"))

        assertEquals(details, exception.error.details)
    }

    @Test
    fun `should throw exception when updating with a birthdate that is the current date`() {
        val invalidParams = params.copy(birthdate = LocalDate.now().format(formatter))

        doNothing().`when`(authorRepository).update(dto)
        Mockito.`when`(authorRepository.existsById(1)).thenReturn(true)

        val exception = assertThrows<CustomResponseStatusException> {
            authorService.update("1", invalidParams)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("birthdate", "現在日より過去の日付を入力してください。"))

        assertEquals(details, exception.error.details)
    }

    @Test
    fun `should throw exception when updating with a birthdate that is the future date`() {
        val invalidParams = params.copy(birthdate = LocalDate.now().plusDays(1).format(formatter))

        doNothing().`when`(authorRepository).update(dto)
        Mockito.`when`(authorRepository.existsById(1)).thenReturn(true)

        val exception = assertThrows<CustomResponseStatusException> {
            authorService.update("1", invalidParams)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("birthdate", "現在日より過去の日付を入力してください。"))

        assertEquals(details, exception.error.details)
    }

    @Test
    fun `should throw exception when updating with an invalid date format`() {
        val invalidParams = params.copy(birthdate = "20200101")

        doNothing().`when`(authorRepository).update(dto)
        Mockito.`when`(authorRepository.existsById(1)).thenReturn(true)

        val exception = assertThrows<CustomResponseStatusException> {
            authorService.update("1", invalidParams)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("birthdate", "正しい日付を入力してください（YYYY-MM-DD）。"))

        assertEquals(details, exception.error.details)
    }

    @Test
    fun `should throw exception when updating with an invalid date range`() {
        val invalidParams = params.copy(birthdate = "2021-02-29")

        doNothing().`when`(authorRepository).update(dto)
        Mockito.`when`(authorRepository.existsById(1)).thenReturn(true)

        val exception = assertThrows<CustomResponseStatusException> {
            authorService.update("1", invalidParams)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("birthdate", "正しい日付を入力してください（YYYY-MM-DD）。"))

        assertEquals(details, exception.error.details)
    }
}