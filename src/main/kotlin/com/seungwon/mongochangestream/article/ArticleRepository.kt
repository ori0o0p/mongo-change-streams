package com.seungwon.mongochangestream.article

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.stereotype.Repository

@Repository
@EnableReactiveMongoRepositories
internal interface ArticleRepository: ReactiveMongoRepository<Article, String> {
}