package com.example.library.model.books

import com.example.library.db.tables.records.BooksRecord
import com.example.library.model.authors.AuthorDTO
import com.fasterxml.jackson.annotation.JsonProperty

data class BookDTO(
    @JsonProperty("id") var id: Int,
    @JsonProperty("title") var title: String,
    @JsonProperty("price") var price: Int,
    @JsonProperty("isPublished") var isPublished: Boolean,
    @JsonProperty("authors") var authors: List<AuthorDTO>
) {
    constructor(record: BooksRecord, authors: List<AuthorDTO>) : this(record.id, record.title, record.price, record.isPublished, authors)
    constructor(record: BooksRecord) : this(record, emptyList())
    constructor(id: Int, title: String, price: Int, isPublished: Boolean) : this(id, title, price, isPublished, emptyList())
    constructor(): this(0, "", 0, false, emptyList())
}
