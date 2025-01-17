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
class AuthorBookRepositoryTest(
    @Autowired private val authorBookRepository: AuthorBookRepository,
    @Autowired private val authorRepository: AuthorRepository,
    @Autowired private val bookRepository: BookRepository
) {

    private val bookDTO = BookDTO(0, "title", 1000, false)
    private val authorDTO = AuthorDTO(0, "name", LocalDate.of(2020, 1, 1))

    @Test
    @Transactional
    fun `should create and remove AuthorBook`() {
        val bookId = bookRepository.create(bookDTO)
        val authorId1 = authorRepository.create(authorDTO)
        val authorId2 = authorRepository.create(authorDTO)
        val authorIds = listOf(authorId1, authorId2)

        authorBookRepository.createAuthorsToBook(bookId, authorIds)
        val insertedData = authorBookRepository.findAuthorIdByBookId(bookId)
        assertEquals(authorIds, insertedData)

        authorBookRepository.removeByBook(bookId)
        val removedData = authorBookRepository.findAuthorIdByBookId(bookId)
        assertEquals(0, removedData.size)
    }
}