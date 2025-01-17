package com.example.library.model.authors

import com.example.library.db.tables.records.AuthorsRecord
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class AuthorDTO(
    @JsonProperty("id") var id: Int,
    @JsonProperty("name") var name: String,
    @JsonProperty("birthdate") var birthdate: LocalDate
) {
    constructor(record: AuthorsRecord) : this(record.id, record.name, record.birthdate)
    constructor() : this(0, "", LocalDate.now())
}
