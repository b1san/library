package com.example.library.controller

import com.example.library.model.authors.AuthorRequest
import com.example.library.service.author.AuthorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/author")
class AuthorController(@Autowired private var authorService: AuthorService) {

    /**
     * Authorリソースの作成
     *
     * @param params リクエストパラメーター（リクエストボディ）
     */
    @PostMapping
    fun create(@RequestBody params: AuthorRequest) {
        authorService.create(params)
    }

    /**
     * Authorリソースの更新
     *
     * @param id Authorのid（パスパラメーター）
     * @param params リクエストパラメーター（リクエストボディ）
     */
    @PutMapping("{id}")
    fun update(@PathVariable("id") id: String, @RequestBody params: AuthorRequest) {
        authorService.update(id, params)
    }
}