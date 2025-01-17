package com.example.library.service

import com.example.library.exception.CustomResponseStatusException
import com.example.library.model.books.BookDTO
import com.example.library.model.books.BookRequest
import com.example.library.model.error.ErrorDetail
import com.example.library.repository.author.AuthorRepository
import com.example.library.repository.authorbook.AuthorBookRepository
import com.example.library.repository.book.BookRepository
import com.example.library.service.book.BookServiceImpl
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
import kotlin.test.assertEquals

@SpringBootTest
class BookServiceTest {

    @Mock
    lateinit var bookRepository: BookRepository

    @Mock
    lateinit var authorRepository: AuthorRepository

    @Mock
    lateinit var authorBookRepository: AuthorBookRepository

    @InjectMocks
    lateinit var bookService: BookServiceImpl

    lateinit var params: BookRequest
    lateinit var dto: BookDTO

    @BeforeEach
    fun setup() {
        val title = "a".repeat(255)
        val price = 1000
        val isPublished = true
        val authors = listOf(1, 2)
        params = BookRequest(title, price, isPublished, authors)
        dto = BookDTO(1, title, price, isPublished)
    }

    //=================================================================================================================
    // create method
    //=================================================================================================================
    @Test
    fun `should create Book when valid params are provided`() {
        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        Mockito.`when`(bookRepository.create(dto)).thenReturn(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        assertDoesNotThrow {
            bookService.create(params)
        }
    }

    @Test
    fun `should throw exception when creating with null params`() {
        val invalidParams = params.copy(title = null, price = null, isPublished = null, authors = null)

        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        Mockito.`when`(bookRepository.create(dto)).thenReturn(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        val exception = assertThrows<CustomResponseStatusException> {
            bookService.create(invalidParams)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("title", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("price", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("isPublished", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("authors", "必須入力です。値を設定してください。"))

        assertEquals(details, exception.error.details)
    }

    @Test
    fun `should throw exception when creating with empty params`() {
        val invalidParams = params.copy(title = "", authors = emptyList())

        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        Mockito.`when`(bookRepository.create(dto)).thenReturn(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        val exception = assertThrows<CustomResponseStatusException> {
            bookService.create(invalidParams)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("title", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("authors", "必須入力です。値を設定してください。"))

        assertEquals(details, exception.error.details)
    }

    @Test
    fun `should throw exception when creating with character limit`() {
        val invalidParams = params.copy(title = "a".repeat(256))

        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        Mockito.`when`(bookRepository.create(dto)).thenReturn(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        val exception = assertThrows<CustomResponseStatusException> {
            bookService.create(invalidParams)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("title", "最大文字数を超えました。255文字以内で入力してください。"))

        assertEquals(exception.error.details, details)
    }

    @Test
    fun `should create Book when valid params are provided with a zero price value`() {
        val validParams = params.copy(price = 0)

        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        Mockito.`when`(bookRepository.create(dto)).thenReturn(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        assertDoesNotThrow {
            bookService.create(validParams)
        }
    }

    @Test
    fun `should throw exception when creating with a negative price value`() {
        val invalidParams = params.copy(price = -1)

        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        Mockito.`when`(bookRepository.create(dto)).thenReturn(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        val exception = assertThrows<CustomResponseStatusException> {
            bookService.create(invalidParams)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("price", "0以上の数値を入力してください。"))

        assertEquals(exception.error.details, details)
    }

    @Test
    fun `should create Book when valid params are provided with isPublished set false`() {
        val validParams = params.copy(isPublished = false)

        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        Mockito.`when`(bookRepository.create(dto)).thenReturn(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        assertDoesNotThrow {
            bookService.create(validParams)
        }
    }

    @Test
    fun `should throw exception when creating with the duplicated Author`() {
        val invalidParams = params.copy(authors = listOf(1, 2, 1))

        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        Mockito.`when`(bookRepository.create(dto)).thenReturn(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        val exception = assertThrows<CustomResponseStatusException> {
            bookService.create(invalidParams)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("authors", "著者が重複して設定されています。重複しないように設定してください。"))

        assertEquals(exception.error.details, details)
    }

    @Test
    fun `should throw exception when creating with Author not exists`() {
        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(false)
        Mockito.`when`(bookRepository.create(dto)).thenReturn(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        val exception = assertThrows<CustomResponseStatusException> {
            bookService.create(params)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("authors", "存在しない著者が設定されています。著者を正しく設定してください。"))

        assertEquals(exception.error.details, details)
    }

    //=================================================================================================================
    // update method
    //=================================================================================================================
    @Test
    fun `should update Book when valid params are provided`() {
        Mockito.`when`(bookRepository.findById(1)).thenReturn(dto)
        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        doNothing().`when`(bookRepository).update(dto)
        doNothing().`when`(authorBookRepository).removeByBook(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        assertDoesNotThrow {
            bookService.update("1", params)
        }
    }

    @Test
    fun `should throw exception when updating with an invalid id`() {
        Mockito.`when`(bookRepository.findById(1)).thenReturn(dto)
        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        doNothing().`when`(bookRepository).update(dto)
        doNothing().`when`(authorBookRepository).removeByBook(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        val exception = assertThrows<CustomResponseStatusException> {
            bookService.update("0", params)
        }
        assertEquals(HttpStatus.NOT_FOUND, exception.status)
    }

    @Test
    fun `should throw exception when updating with not exists data`() {
        Mockito.`when`(bookRepository.findById(1)).thenReturn(null)
        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        doNothing().`when`(bookRepository).update(dto)
        doNothing().`when`(authorBookRepository).removeByBook(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        val exception = assertThrows<CustomResponseStatusException> {
            bookService.update("1", params)
        }
        assertEquals(HttpStatus.NOT_FOUND, exception.status)
    }

    @Test
    fun `should throw exception when updating with null params`() {
        val invalidParams = params.copy(title = null, price = null, isPublished = null, authors = null)

        Mockito.`when`(bookRepository.findById(1)).thenReturn(dto)
        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        doNothing().`when`(bookRepository).update(dto)
        doNothing().`when`(authorBookRepository).removeByBook(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        val exception = assertThrows<CustomResponseStatusException> {
            bookService.update("1", invalidParams)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("title", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("price", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("isPublished", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("authors", "必須入力です。値を設定してください。"))

        assertEquals(details, exception.error.details)
    }

    @Test
    fun `should throw exception when updating with empty params`() {
        val invalidParams = params.copy(title = "", authors = emptyList())

        Mockito.`when`(bookRepository.findById(1)).thenReturn(dto)
        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        doNothing().`when`(bookRepository).update(dto)
        doNothing().`when`(authorBookRepository).removeByBook(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        val exception = assertThrows<CustomResponseStatusException> {
            bookService.update("1", invalidParams)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("title", "必須入力です。値を入力してください。"))
        details.add(ErrorDetail("authors", "必須入力です。値を設定してください。"))

        assertEquals(details, exception.error.details)
    }

    @Test
    fun `should throw exception when updating with character limit`() {
        val invalidParams = params.copy(title = "a".repeat(256))

        Mockito.`when`(bookRepository.findById(1)).thenReturn(dto)
        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        doNothing().`when`(bookRepository).update(dto)
        doNothing().`when`(authorBookRepository).removeByBook(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        val exception = assertThrows<CustomResponseStatusException> {
            bookService.update("1", invalidParams)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("title", "最大文字数を超えました。255文字以内で入力してください。"))

        assertEquals(exception.error.details, details)
    }

    @Test
    fun `should update Book when valid params are provided with a zero price value`() {
        val validParams = params.copy(price = 0)

        Mockito.`when`(bookRepository.findById(1)).thenReturn(dto)
        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        doNothing().`when`(bookRepository).update(dto)
        doNothing().`when`(authorBookRepository).removeByBook(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        assertDoesNotThrow {
            bookService.update("1", validParams)
        }
    }

    @Test
    fun `should throw exception when updating with a negative price value`() {
        val invalidParams = params.copy(price = -1)

        Mockito.`when`(bookRepository.findById(1)).thenReturn(dto)
        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        doNothing().`when`(bookRepository).update(dto)
        doNothing().`when`(authorBookRepository).removeByBook(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        val exception = assertThrows<CustomResponseStatusException> {
            bookService.update("1", invalidParams)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("price", "0以上の数値を入力してください。"))

        assertEquals(exception.error.details, details)
    }

    @Test
    fun `should update Book when valid params are provided with isPublished set from false to true`() {
        val currentDTO = dto.copy(isPublished = false)

        Mockito.`when`(bookRepository.findById(1)).thenReturn(currentDTO)
        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        doNothing().`when`(bookRepository).update(dto)
        doNothing().`when`(authorBookRepository).removeByBook(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        assertDoesNotThrow {
            bookService.update("1", params)
        }
    }

    @Test
    fun `should update Book when valid params are provided with isPublished set from false to false`() {
        val currentDTO = dto.copy(isPublished = false)
        val validParams = params.copy(isPublished = false)

        Mockito.`when`(bookRepository.findById(1)).thenReturn(currentDTO)
        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        doNothing().`when`(bookRepository).update(dto)
        doNothing().`when`(authorBookRepository).removeByBook(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        assertDoesNotThrow {
            bookService.update("1", validParams)
        }
    }

    @Test
    fun `should throw an exception when updating with isPublished set from true to false`() {
        val invalidParams = params.copy(isPublished = false)

        Mockito.`when`(bookRepository.findById(1)).thenReturn(dto)
        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        doNothing().`when`(bookRepository).update(dto)
        doNothing().`when`(authorBookRepository).removeByBook(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        val exception = assertThrows<CustomResponseStatusException> {
            bookService.update("1", invalidParams)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("isPublished", "出版済みを未出版に設定することはできません。"))

        assertEquals(exception.error.details, details)
    }

    @Test
    fun `should throw exception when updating with the duplicated Author`() {
        val invalidParams = params.copy(authors = listOf(1, 2, 1))

        Mockito.`when`(bookRepository.findById(1)).thenReturn(dto)
        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(true)
        doNothing().`when`(bookRepository).update(dto)
        doNothing().`when`(authorBookRepository).removeByBook(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        val exception = assertThrows<CustomResponseStatusException> {
            bookService.update("1", invalidParams)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("authors", "著者が重複して設定されています。重複しないように設定してください。"))

        assertEquals(exception.error.details, details)
    }

    @Test
    fun `should throw exception when updating with Author not exists`() {
        Mockito.`when`(bookRepository.findById(1)).thenReturn(dto)
        Mockito.`when`(authorRepository.existsByIds(params.authors!!)).thenReturn(false)
        doNothing().`when`(bookRepository).update(dto)
        doNothing().`when`(authorBookRepository).removeByBook(1)
        doNothing().`when`(authorBookRepository).createAuthorsToBook(1, params.authors!!)

        val exception = assertThrows<CustomResponseStatusException> {
            bookService.update("1", params)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.status)

        val details = mutableListOf<ErrorDetail>()
        details.add(ErrorDetail("authors", "存在しない著者が設定されています。著者を正しく設定してください。"))

        assertEquals(exception.error.details, details)
    }
}