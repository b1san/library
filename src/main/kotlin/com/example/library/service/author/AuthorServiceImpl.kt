package com.example.library.service.author

import com.example.library.exception.CustomResponseStatusException
import com.example.library.exception.ErrorType
import com.example.library.model.authors.AuthorDTO
import com.example.library.model.authors.AuthorRequest
import com.example.library.model.error.ErrorDetail
import com.example.library.repository.author.AuthorRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class AuthorServiceImpl(@Autowired private val authorRepository: AuthorRepository) : AuthorService {

    /**
     * Authorsデータをチェックして登録
     *
     * @param params リクエストパラメーター
     * @exception CustomResponseStatusException
     */
    @Transactional
    override fun create(params: AuthorRequest) {
        validate(params)

        val dto = AuthorDTO(0, params.name!!, strToDate(params.birthdate!!))
        authorRepository.create(dto)
    }

    /**
     * Authorsデータをチェックして更新
     *
     * @param strId 更新対象のAuthorのid文字列
     * @param params リクエストパラメーター
     * @exception CustomResponseStatusException
     */
    @Transactional
    override fun update(strId: String, params: AuthorRequest) {
        val id = strId.toIntOrNull() ?: 0
        if (id <= 0 || !authorRepository.existsById(id)) {
            throw CustomResponseStatusException(ErrorType.NO_DATA)
        }
        validate(params)

        val dto = AuthorDTO(id, params.name!!, strToDate(params.birthdate!!))
        authorRepository.update(dto)
    }

    /**
     * Authorのバリデーションを実行
     * エラーが存在する場合は例外をスロー
     *
     * @param params リクエストパラメーター
     * @exception CustomResponseStatusException
     */
    private fun validate(params: AuthorRequest) {
        val details = mutableListOf<ErrorDetail>()
        //Name
        if (params.name.isNullOrEmpty()) {
            details.add(ErrorDetail("name", "必須入力です。値を入力してください。"))
        } else if (params.name!!.length > 255) {
            details.add(ErrorDetail("name", "最大文字数を超えました。255文字以内で入力してください。"))
        }

        //birthDate
        if (params.birthdate.isNullOrEmpty()) {
            details.add(ErrorDetail("birthdate", "必須入力です。値を入力してください。"))
        } else {
            try {
                val birthdate = strToDate(params.birthdate!!)
                if (!LocalDate.now().isAfter(birthdate)) {
                   details.add(ErrorDetail("birthdate", "現在日より過去の日付を入力してください。"))
                }
            } catch (e: RuntimeException) {
                details.add(ErrorDetail("birthdate", "正しい日付を入力してください（YYYY-MM-DD）。"))
            }
        }

        if (details.size > 0) {
            throw CustomResponseStatusException(ErrorType.INVALID, details)
        }
    }

    /**
     * 文字列を日付型（LocalDate）に変換
     *
     * @param str 変換対象文字列
     * @return 日付
     * @exception RuntimeException
     */
    private fun strToDate(str: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(str, formatter)
        //自動調整を考慮しての判定（自動調整：2024-04-31 → 2024-04-30）
        if (str == date.format(formatter)) {
            return date
        } else {
            throw RuntimeException()
        }
    }
}