package com.example.library.model.books

data class BookRequest(var title: String?, var price: Int?, var isPublished: Boolean?, var authors: List<Int>?)
