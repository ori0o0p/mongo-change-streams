package com.seungwon.mongochangestream.article

import org.springframework.data.mongodb.core.mapping.Document

@Document("articles")
class Article(
    var id: String?,
    var title: String,
    var content: String?
) {
    constructor(title: String, content: String): this(null, title, content)
}