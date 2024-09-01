package com.seungwon.mongochangestream.article

import org.springframework.data.mongodb.core.mapping.Document

@Document("articles")
internal class Article(
    var id: String?,
    var title: String,
    var content: String?
) {
}