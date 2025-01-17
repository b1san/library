package com.example.library.repository.authorbook

interface AuthorBookRepository {
    fun createAuthorsToBook(bookId: Int, authorIds: List<Int>)
    fun removeByBook(bookId: Int)
    fun findAuthorIdByBookId(bookId: Int): List<Int>
}