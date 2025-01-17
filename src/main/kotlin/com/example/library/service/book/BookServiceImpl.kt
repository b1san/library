package com.example.library.service.book

import com.example.library.exception.CustomResponseStatusException
import com.example.library.exception.ErrorType
import com.example.library.model.books.BookDTO
import com.example.library.model.books.BookRequest
import com.example.library.model.error.ErrorDetail
import com.example.library.repository.author.AuthorRepository
import com.example.library.repository.authorbook.AuthorBookRepository
import com.example.library.repository.book.BookRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookServiceImpl(
    @Autowired private val bookRepository: BookRepository,
    @Autowired private val authorBookRepository: AuthorBookRepository,
    @Autowired private val authorRepository: AuthorRepository
) : BookService {

    /**
     * Booksデータをチェックして登録
     *
     * @param params リクエストパラメーター
     * @throws CustomResponseStatusException
     */
    @Transactional
    override fun create(params: BookRequest) {
        validate(params, null)

        val bookDTO = BookDTO(0, params.title!!, params.price!!, params.isPublished!!)

        val bookId = bookRepository.create(bookDTO)
        authorBookRepository.createAuthorsToBook(bookId, params.authors!!)
    }

    /**
     * Booksデータをチェックして更新
     *
     * @param strId 更新対象のBooksのid文字列
     * @param params リクエストパラメーター
     * @throws CustomResponseStatusException
     */
    @Transactional
    override fun update(strId: String, params: BookRequest) {
        val bookId = strId.toIntOrNull() ?: 0
        if (bookId <= 0) {
            throw CustomResponseStatusException(ErrorType.NO_DATA)
        }
        val currentBookDTO = bookRepository.findById(bookId) ?: throw CustomResponseStatusException(ErrorType.NO_DATA)
        validate(params, currentBookDTO)

        val bookDTO = BookDTO(bookId, params.title!!, params.price!!, params.isPublished!!)

        bookRepository.update(bookDTO)
        authorBookRepository.removeByBook(bookId)
        authorBookRepository.createAuthorsToBook(bookId, params.authors!!)
    }

    /**
     * Authorsのidに該当するBooksデータのリストを検索
     * 対象が存在しない場合は例外スロー
     *
     * @param strAuthorId Authorのid文字列
     * @throws CustomResponseStatusException
     */
    override fun findByAuthor(strAuthorId: String?): List<BookDTO> {
        val authorId = strAuthorId?.toIntOrNull() ?: 0
        if (authorId <= 0) {
            throw CustomResponseStatusException(ErrorType.NO_DATA)
        }

        val bookDTOs = bookRepository.findByAuthorId(authorId)
        if (bookDTOs.isEmpty()) {
            throw CustomResponseStatusException(ErrorType.NO_DATA)
        }
        return bookDTOs
    }

    /**
     * Booksのバリデーションを実行
     * エラーが存在する場合は例外をスロー
     *
     * @param params リクエストパラメーター
     * @exception CustomResponseStatusException
     */
    private fun validate(params: BookRequest, book: BookDTO?) {
        val details = mutableListOf<ErrorDetail>()

        //Title
        if (params.title.isNullOrEmpty()) {
            details.add(ErrorDetail("title", "必須入力です。値を入力してください。"))
        } else if (params.title!!.length > 255) {
            details.add(ErrorDetail("title", "最大文字数を超えました。255文字以内で入力してください。"))
        }

        //Price
        if (params.price == null) {
            details.add(ErrorDetail("price", "必須入力です。値を入力してください。"))
        } else if (params.price!! < 0) {
            details.add(ErrorDetail("price", "0以上の数値を入力してください。"))
        }

        //Is Published
        if (params.isPublished == null) {
            details.add(ErrorDetail("isPublished", "必須入力です。値を入力してください。"))
        } else if (book != null && book.isPublished && !params.isPublished!!){
            details.add(ErrorDetail("isPublished", "出版済みを未出版に設定することはできません。"))
        }

        //Authors
        if (params.authors.isNullOrEmpty()) {
            details.add(ErrorDetail("authors", "必須入力です。値を設定してください。"))
        } else if (params.authors!!.size != params.authors!!.distinct().size) {
            details.add(ErrorDetail("authors", "著者が重複して設定されています。重複しないように設定してください。"))
        } else if (!authorRepository.existsByIds(params.authors!!)) {
            details.add(ErrorDetail("authors", "存在しない著者が設定されています。著者を正しく設定してください。"))
        }

        if (details.size > 0) {
            throw CustomResponseStatusException(ErrorType.INVALID, details)
        }
    }
}