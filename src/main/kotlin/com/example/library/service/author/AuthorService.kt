package com.example.library.service.author

import com.example.library.exception.CustomResponseStatusException
import com.example.library.model.authors.AuthorRequest

interface AuthorService {
    @Throws(CustomResponseStatusException::class)
    fun create(params: AuthorRequest)

    @Throws(CustomResponseStatusException::class)
    fun update(strId: String, params: AuthorRequest)
}