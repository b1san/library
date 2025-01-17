package com.example.library.controller

import com.example.library.model.books.BookDTO
import com.example.library.model.books.BookRequest
import com.example.library.service.book.BookService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/book")
class BookController(@Autowired private var bookService: BookService) {

    /**
     * Booksリソースを作成
     *
     * @param params リクエストパラメーター（リクエストボディ）
     */
    @PostMapping
    fun create(@RequestBody params: BookRequest) {
        bookService.create(params)
    }

    /**
     * Booksリソースの更新
     *
     * @param id Booksのid（パスパラメーター）
     * @param params リクエストパラメーター（リクエストボディ）
     */
    @PutMapping("{id}")
    fun update(@PathVariable("id") id: String, @RequestBody params: BookRequest) {
        bookService.update(id, params)
    }

    /**
     * Booksの検索
     *
     * @params authorId Authorのid（クエリパラメーター）
     * @return Booksのリスト
     */
    @GetMapping
    fun findByAuthor(@RequestParam authorId: String): List<BookDTO> {
        return bookService.findByAuthor(authorId)
    }
}