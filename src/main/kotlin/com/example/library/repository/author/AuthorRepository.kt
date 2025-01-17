package com.example.library.repository.author

import com.example.library.model.authors.AuthorDTO

interface AuthorRepository {
    fun create(dto: AuthorDTO): Int
    fun update(dto: AuthorDTO)
    fun findById(id: Int): AuthorDTO?
    fun existsById(id: Int): Boolean
    fun existsByIds(ids: List<Int>): Boolean
}