# Library Project

株式会社クオカードが実施しているコーディングテストのためのプロジェクト。

> https://quo-digital.hatenablog.com/entry/2024/03/22/143542

## セットアップ

### 前提条件

- Java： 17
- 言語：Kotlin
- フレームワーク：Spring Boot, JOOQ
- プラグイン:
  - JOOQ Access Layer
  - Flyway Migration
  - PostgreSQL Driver
  - Docker Compose Support
  - Spring Web
  - Spring Boot DevTools
  - Jackson Datatype: JSR310
- ビルドツール：Gradle - Groovy
- DB：PostgreSQL（Dockerにて構築）
- その他：Docker

### 手順

#### 1. Dockerコンテナの起動

```bash
$ docker compose up -d
```

#### 2. JOOQコード生成

```bash
$ ./gradlew :jooqCodegen
```
   
#### 3. ビルド

```bash
$ ./gradlew build -x test
```

#### 4. Spring Boot実行

```bash
$ ./gradlew bootRun
```

※ 起動時にDockerも一緒に起動されるため、事前に起動する必要はない

#### 5. テストの実行

```bash
$ ./gradlew test
```
#### 6. Dockerコンテナの停止

```bash
$ docker compose down
```

※ Dockerコマンドで起動した場合は、Spring Bootの実行停止でもDockerは停止されない

## データベース

### authors

| カラム名      | データ型         | 制約                        | 説明                    |
|:-------------|:---------------|:----------------------------|:-----------------------|
| `id`         | `SERIAL`       | `PRIMARY KEY`               | 著者ID（自動生成）        |
| `name`       | `VARCHAR(255)` | `NOT NULL`                  | 著者名                  |
| `birthdate`  | `DATE`         | `NOT NULL`                  | 著者の生年月日            |
| `created_at` | `TIMESTAMP`    | `DEFAULT CURRENT_TIMESTAMP` | レコード作成日時          |
| `updated_at` | `TIMESTAMP`    | `DEFAULT CURRENT_TIMESTAMP` | レコード更新日時（自動更新）|

### books

| カラム名        | データ型         | 制約                        | 説明                    |
|:---------------|:---------------|:----------------------------|:-----------------------|
| `id`           | `SERIAL`       | `PRIMARY KEY`               | 書籍ID（自動生成  ）      |
| `title`        | `VARCHAR(255)` | `NOT NULL`                  | 書籍タイトル              |
| `price`        | `INTEGER`      | `NOT NULL`                  | 価格                    |
| `is_published` | `BOOLEAN`      | `DEFAULT FALSE`             | 書籍が出版済みか否か       |
| `created_at`   | `TIMESTAMP`    | `DEFAULT CURRENT_TIMESTAMP` | レコード作成日時          |
| `updated_at`   | `TIMESTAMP`    | `DEFAULT CURRENT_TIMESTAMP` | レコード更新日時（自動更新）|

### authors_books

| カラム名        | データ型      | 制約                        | 説明           |
|:---------------|:------------|:----------------------------|:--------------|
| `author_id`    | `INTEGER`   | `PRIMARY KEY`               | 著者ID         |
| `book_id`      | `INTEGER`   | `PRIMARY KEY`               | 書籍ID         |
| `created_at`   | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | レコード作成日時 |

## API

### 共通

#### データフォーマット

- リクエストボディ：JSON
- レスポンスボディ：JSON

#### HttpStatus

- `200` `OK`：成功
- `400` `Bad Request`：論理エラー
- `404` `Not Found`：リソースが存在しない

#### エラーレスポンス

- エラーフォーマット

```json
{
  "error": {
    "message": String,
    "details": [
      {
        "field": String,
        "message": String
      }
    ]
  }
}
```

- エラー例1

```json
{
  "error": {
    "code": 1,
    "message": "バリデーションエラーです。",
    "details": [
      {
        "field": "name",
        "message": "必須入力です。値を入力してください。"
      },
      {
        "field": "birthdate",
        "message": "正しい日付を入力してください（YYYY-MM-DD）。"
      }
    ]
  }
}
```

- エラー例2

```json
{
  "error": {
    "message": "対象のデータが存在しません。"
  }
}
```

### 著者登録

- エンドポイント：`/api/author`
- メソッド：`POST`
- HTTPステータス：`200`, `400`

#### パラメーター

| 項目名       | 型       | 説明    | バリデーション                               |
|:------------|:---------|:-------|:------------------------------------------|
| `name`      | `String` | 著者名  | 必須、桁数(255）                            |
| `birthDate` | `String` | 生年月日| 必須、フォーマット（yyyy-MM-dd）、現在日より過去 |

#### レスポンス

なし

### 著者更新

- エンドポイント：`/api/author/{id}`
- メソッド：`PUT`
- HTTPステータス：`200`, `400`, `404`

#### パラメーター

| 項目名       | 型       | 説明    | バリデーション                               |
|:------------|:---------|:-------|:------------------------------------------|
| `name`      | `String` | 著者名  | 必須、桁数(255）                            |
| `birthDate` | `String` | 生年月日| 必須、フォーマット（yyyy-MM-dd）、現在日より過去 |

#### レスポンス

なし

### 書籍登録

- エンドポイント：`/api/book`
- メソッド：`POST`
- HTTPステータス：`200`, `400`

#### パラメーター

| 項目名         | 型               | 説明                            | バリデーション                         |
|:--------------|:----------------|:--------------------------------|:-------------------------------------|
| `title`       | `String`        | 書籍タイトル                      | 必須、桁数(255）                       |
| `price`       | `Number`        | 価格                             | 必須、数値、0以上                      |
| `isPublished` | `Boolean`       | 出版済みか否か（デフォルト：`false`）| 真偽値（`true`/`false`）              |
| `authors`     | `Array<Number>` | 著者IDのリスト                    | 必須、1人以上、被りなし、すべての著者が存在 |

#### レスポンス

なし

### 書籍更新

- エンドポイント：`/api/book/{id}`
- メソッド：`PUT`
- HTTPステータス：`200`, `400`, `404`

#### パラメーター

| 項目名         | 型               | 説明                            | バリデーション                                       |
|:--------------|:----------------|:--------------------------------|:---------------------------------------------------|
| `title`       | `String`        | 書籍タイトル                      | 必須、桁数(255）                                     |
| `price`       | `Number`        | 価格                             | 必須、数値、0以上                                    |
| `isPublished` | `Boolean`       | 出版済みか否か（デフォルト：`false`）| 真偽値（`true`/`false`）、既に`true`の場合`false`は不可 |
| `authors`     | `Array<Number>` | 著者IDのリスト                    | 必須、1人以上、被りなし、すべての著者が存在               |

#### レスポンス

なし

### 書籍取得

- エンドポイント：`/api/book?authorId={id}`
- メソッド：`GET`
- HTTPステータス：`200`, `400`, `404`

#### パラメーター

| 項目名        | 型        | 説明   | バリデーション|
|:-------------|:----------|:------|:------------|
| `authorId`   | `Integer` | 著者ID | 必須        |

#### レスポンス

```json
[
  {
    "id": Number,
    "title": String,
    "price": Number,
    "isPublished": Boolean,
    "authors": [
      "id": Number,
      "name": String,
      "birthDate": String,
    ],
    "timestamp": String
  }
]
```
