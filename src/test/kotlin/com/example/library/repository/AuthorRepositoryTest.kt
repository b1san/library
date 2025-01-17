package com.example.library.repository

import com.example.library.model.authors.AuthorDTO
import com.example.library.repository.author.AuthorRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import kotlin.test.assertEquals

@SpringBootTest
class AuthorRepositoryTest(@Autowired private val authorRepository: AuthorRepository) {

    var dto = AuthorDTO(0, "name", LocalDate.of(2020, 1, 1))

    @Test
    @Transactional
    fun `should create Author`() {
        val id = authorRepository.create(dto)
        val insertedDTO = authorRepository.findById(id)
        val originDTO = dto.copy(id = id)
        assertEquals(originDTO, insertedDTO)
    }

    @Test
    @Transactional
    fun `should update Author`() {
        val id = authorRepository.create(dto)
        val updateDTO = dto.copy(id = id, name = "name1", birthdate = LocalDate.of(2021, 2, 2))
        authorRepository.update(updateDTO)
        val updatedDTO = authorRepository.findById(id)
        assertEquals(updateDTO, updatedDTO)
    }

    @Test
    @Transactional
    fun `should check if Author exists`() {
        val id = authorRepository.create(dto)
        val result = authorRepository.existsById(id)
        assertEquals(true, result)
    }

    @Test
    @Transactional
    fun `should check if Author dose not exist`() {
        val id = authorRepository.create(dto)
        val result = authorRepository.existsById(id + 1)
        assertEquals(false, result)
    }

    @Test
    @Transactional
    fun `should check if multiple Authors exists`() {
        val id1 = authorRepository.create(dto)
        val id2 = authorRepository.create(dto)
        val ids = listOf(id1, id2)
        val result = authorRepository.existsByIds(ids)
        assertEquals(true, result)
    }

    @Test
    @Transactional
    fun `should check if some Authors dose not exist`() {
        val id = authorRepository.create(dto)
        val ids = listOf(id, id + 1)
        val result = authorRepository.existsByIds(ids)
        assertEquals(false, result)
    }
}