package com.example.library.repository.author

import com.example.library.db.tables.Authors
import com.example.library.db.tables.Books
import com.example.library.model.authors.AuthorDTO
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class AuthorRepositoryImpl(@Autowired private val dsl: DSLContext) : AuthorRepository {

    /**
     * Authorsのデータを作成
     *
     * @param dto AuthorsのDTO
     * @return 作成したAuthorsのid
     */
    override fun create(dto: AuthorDTO): Int {
        val result = dsl.insertInto(Authors.AUTHORS)
            .set(Authors.AUTHORS.NAME, dto.name)
            .set(Authors.AUTHORS.BIRTHDATE, dto.birthdate )
            .returningResult(Authors.AUTHORS.ID)
            .fetchOne()
        return result?.getValue(Books.BOOKS.ID) ?: throw IllegalStateException("Failed to retrieve ID.")
    }

    /**
     * Authorsのデータを更新
     *
     * @param dto AuthorのDTO
     */
    override fun update(dto: AuthorDTO) {
        dsl.update(Authors.AUTHORS)
            .set(Authors.AUTHORS.NAME, dto.name)
            .set(Authors.AUTHORS.BIRTHDATE, dto.birthdate)
            .where(Authors.AUTHORS.ID.eq(dto.id))
            .execute();
    }

    /**
     * idに該当するAuthorsデータを取得｀
     *
     * @param id Authorsのid
     * @return AuthorsのDTO、存在しない場合はnull
     */
    override fun findById(id: Int): AuthorDTO? {
        val record = dsl.selectFrom(Authors.AUTHORS)
            .where(Authors.AUTHORS.ID.eq(id))
            .fetchOne()
        if (record == null) {
            return null
        }
        return AuthorDTO(record)
    }

    /**
     * idに該当するAuthorsレコードの存在有無を判定
     *
     * @param id Authorsのid
     * @return あり：true、なし：false
     */
    override fun existsById(id: Int): Boolean {
        return dsl.fetchExists(
            DSL.selectFrom(Authors.AUTHORS)
                .where(Authors.AUTHORS.ID.eq(id))
        )
    }

    /**
     * idリストのすべてが存在するかを判定
     *
     * @param ids Authorsのidリスト
     * @return すべてあり：true、いずれかなし：false
     */
    override fun existsByIds(ids: List<Int>): Boolean {
        val list = dsl.selectFrom(Authors.AUTHORS)
                    .where(Authors.AUTHORS.ID.`in`(ids))
                    .fetch()
        return list.size == ids.size
    }
}