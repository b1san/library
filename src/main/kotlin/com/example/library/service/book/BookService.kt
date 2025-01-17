package com.example.library.service.book

import com.example.library.exception.CustomResponseStatusException
import com.example.library.model.books.BookDTO
import com.example.library.model.books.BookRequest

interface BookService {
    @Throws(CustomResponseStatusException::class)
    fun create(params: BookRequest)

    @Throws(CustomResponseStatusException::class)
    fun update(strId: String, params: BookRequest)

    @Throws(CustomResponseStatusException::class)
    fun findByAuthor(strAuthorId: String?): List<BookDTO>
}