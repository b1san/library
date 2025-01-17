package com.example.library.repository.book

import com.example.library.db.tables.AuthorBook
import com.example.library.db.tables.Authors
import com.example.library.db.tables.Books
import com.example.library.model.authors.AuthorDTO
import com.example.library.model.books.BookDTO
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class BookRepositoryImpl(@Autowired private var dsl: DSLContext) : BookRepository {

    /**
     * Booksデータを作成
     *
     * @param dto BooksのDTO
     * @return 作成したBooksのid
     */
    override fun create(dto: BookDTO): Int {
        val result  = dsl.insertInto(Books.BOOKS)
            .set(Books.BOOKS.TITLE, dto.title)
            .set(Books.BOOKS.PRICE, dto.price)
            .set(Books.BOOKS.IS_PUBLISHED, dto.isPublished)
            .returningResult(Books.BOOKS.ID)
            .fetchOne()
        return result?.getValue(Books.BOOKS.ID) ?: throw IllegalStateException("Failed to retrieve ID.")
    }

    /**
     * Booksデータを更新
     *
     * @param dto BooksのDTO
     */
    override fun update(dto: BookDTO) {
        dsl.update(Books.BOOKS)
            .set(Books.BOOKS.TITLE, dto.title)
            .set(Books.BOOKS.PRICE, dto.price)
            .set(Books.BOOKS.IS_PUBLISHED, dto.isPublished)
            .where(Books.BOOKS.ID.eq(dto.id))
            .execute()
    }

    /**
     * idに該当するBooksを取得
     *
     * @param id Booksのid
     * @return BooksのDTO、存在しない場合はnull
     */
    override fun findById(id: Int): BookDTO? {
        val record = dsl.selectFrom(Books.BOOKS)
            .where(Books.BOOKS.ID.eq(id))
            .fetchOne()
        if (record == null) {
            return null
        }
        return BookDTO(record)
    }

    /**
     * Authorsのidに該当するすべてのBooksのデータを取得
     *
     * @param authorId Authorsのid
     * @return Booksのリスト、該当なしの場合は空リスト
     */
    override fun findByAuthorId(authorId: Int): List<BookDTO> {
        //サブクエリ（Authors.IDに該当するすべてのBooks.IDを取得）
        val subquery = dsl.select(Books.BOOKS.ID)
            .from(Books.BOOKS)
            .leftJoin(AuthorBook.AUTHOR_BOOK).on(Books.BOOKS.ID.eq(AuthorBook.AUTHOR_BOOK.BOOK_ID))
            .where(AuthorBook.AUTHOR_BOOK.AUTHOR_ID.eq(authorId))
        //メインクエリ
        val records = dsl.select(Books.BOOKS.ID, Books.BOOKS.TITLE, Books.BOOKS.PRICE, Books.BOOKS.IS_PUBLISHED,
            Authors.AUTHORS.ID, Authors.AUTHORS.NAME, Authors.AUTHORS.BIRTHDATE)
            .from(Books.BOOKS)
            .leftJoin(AuthorBook.AUTHOR_BOOK).on(Books.BOOKS.ID.eq(AuthorBook.AUTHOR_BOOK.BOOK_ID))
            .leftJoin(Authors.AUTHORS).on(AuthorBook.AUTHOR_BOOK.AUTHOR_ID.eq(Authors.AUTHORS.ID))
            .where(Books.BOOKS.ID.`in`(subquery))
            .orderBy(Books.BOOKS.ID)
            .fetchGroups(Books.BOOKS.ID)

        val result = mutableListOf<BookDTO>()
        records.forEach { (bookId, records) ->
            val authorDTOs = records.map {
                AuthorDTO(it[Authors.AUTHORS.ID], it[Authors.AUTHORS.NAME], it[Authors.AUTHORS.BIRTHDATE])
            }
            val bookDTO = BookDTO(bookId, records.first()[Books.BOOKS.TITLE], records.first()[Books.BOOKS.PRICE],
                records.first()[Books.BOOKS.IS_PUBLISHED], authorDTOs)
            result.add(bookDTO)
        }
        return result
    }
}