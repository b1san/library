package com.example.library.repository.book

import com.example.library.model.books.BookDTO

interface BookRepository {
    fun create(dto: BookDTO): Int
    fun update(dto: BookDTO)
    fun findById(id: Int): BookDTO?
    fun findByAuthorId(authorId: Int): List<BookDTO>
}