package com.example.library.repository

import com.example.library.model.authors.AuthorDTO
import com.example.library.model.books.BookDTO
import com.example.library.repository.author.AuthorRepository
import com.example.library.repository.authorbook.AuthorBookRepository
import com.example.library.repository.book.BookRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import kotlin.test.assertEquals

@SpringBootTest
class BookRepositoryTest(
    @Autowired private val bookRepository: BookRepository,
    @Autowired private val authorRepository: AuthorRepository,
    @Autowired private val authorBookRepository: AuthorBookRepository
) {

    val bookDTO = BookDTO(0, "title", 1000, false)
    val authorDTO = AuthorDTO(0, "name", LocalDate.of(2020, 1, 1))

    @Test
    @Transactional
    fun `should create Book`() {
        val id = bookRepository.create(bookDTO)
        val insertedDTO = bookRepository.findById(id)
        val originDTO = bookDTO.copy(id = id)
        assertEquals(originDTO, insertedDTO)
    }

    @Test
    @Transactional
    fun `should update Book`() {
        val id = bookRepository.create(bookDTO)
        val updateDTO = bookDTO.copy(id, "title1", 2000, true)
        bookRepository.update(updateDTO)
        val updatedDTO = bookRepository.findById(id)
        assertEquals(updateDTO, updatedDTO)
    }

    @Test
    @Transactional
    fun `should find Books by Author when data exists`() {
        val bookId1 = bookRepository.create(bookDTO)
        val bookId2 = bookRepository.create(bookDTO)

        val authorId1 = authorRepository.create(authorDTO)
        val authorId2 = authorRepository.create(authorDTO)
        val authorId3 = authorRepository.create(authorDTO)

        val authorIds1 = listOf(authorId1, authorId2)
        val authorIds2 = listOf(authorId1, authorId3)

        authorBookRepository.createAuthorsToBook(bookId1, authorIds1)
        authorBookRepository.createAuthorsToBook(bookId2, authorIds2)

        val result = bookRepository.findByAuthorId(authorId1)

        val authors1 = listOf(authorDTO.copy(id = authorId1), authorDTO.copy(id = authorId2))
        val authors2 = listOf(authorDTO.copy(id = authorId1), authorDTO.copy(id = authorId3))
        val expected = listOf(
            bookDTO.copy(id = bookId1, authors = authors1),
            bookDTO.copy(id = bookId2, authors = authors2)
        )

        assertEquals(expected, result)
    }

    @Test
    @Transactional
    fun `should find Books by Author when data dose not exist`() {
        val authorId = authorRepository.create(authorDTO)
        val result = bookRepository.findByAuthorId(authorId)
        assertEquals(0, result.size)
    }
}