package com.example.library.repository.authorbook

import com.example.library.db.tables.AuthorBook
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class AuthorBookRepositoryImpl(@Autowired private val dsl: DSLContext) : AuthorBookRepository {

    /**
     * Author_Bookデータを作成
     * Books:Authors = 1:多
     *
     * @param bookId Booksのid
     * @param authorIds Authorsのidリスト
     */
    override fun createAuthorsToBook(bookId: Int, authorIds: List<Int>) {
        authorIds.forEach { authorId ->
            dsl.insertInto(AuthorBook.AUTHOR_BOOK)
                .set(AuthorBook.AUTHOR_BOOK.BOOK_ID, bookId)
                .set(AuthorBook.AUTHOR_BOOK.AUTHOR_ID, authorId)
                .execute()
        }
    }

    /**
     * Author_BookデータのBooks.idによる削除
     *
     * @param bookId Booksのid
     */
    override fun removeByBook(bookId: Int) {
        dsl.delete(AuthorBook.AUTHOR_BOOK)
            .where(AuthorBook.AUTHOR_BOOK.BOOK_ID.eq(bookId))
            .execute()
    }

    /**
     * Booksのidに該当するすべてのAuthorsのidをリストで取得
     *
     * @param bookId Booksのid
     * @return Authorsのidリスト
     */
    override fun findAuthorIdByBookId(bookId: Int): List<Int> {
        val records = dsl.select(AuthorBook.AUTHOR_BOOK.AUTHOR_ID)
            .from(AuthorBook.AUTHOR_BOOK)
            .where(AuthorBook.AUTHOR_BOOK.BOOK_ID.eq(bookId))
            .fetch()
        val result = mutableListOf<Int>()
        records.forEach{ record ->
            result.add(record.value1())
        }
        return result
    }
}