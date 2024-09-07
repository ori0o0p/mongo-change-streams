package com.seungwon.mongochangestream.article

import com.mongodb.client.model.changestream.OperationType
import com.seungwon.mongochangestream.mongo.ChangeStreamBuilder
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
internal class ArticleService(
    private val repository: ArticleRepository,
    private val mongoTemplate: ReactiveMongoTemplate
) {
    fun createNew(title: String, content: String): Mono<Void> {
        val article = Article(
            title = title,
            content = content
        )

        return repository.save(article)
            .then()
    }

    fun edit(articleId: String, title: String, content: String): Mono<Void> =
        repository.findById(articleId)
            .doOnNext {
                it.title = title;
                it.content = content;
            }
            .flatMap { repository.save(it) }
            .then()

    fun subscribe(articleId: String): Flux<Article> {
        return mongoTemplate.changeStream {
            collectionName("articles")
            operationType(OperationType.REPLACE)
            filterBy(Criteria.where("fullDocument._id").`is`(articleId))
            filterBy(Criteria.where("documentKey._id").`is`(ObjectId(articleId)))
        }
    }
}

fun ReactiveMongoTemplate.changeStream(init: ChangeStreamBuilder.() -> Unit): Flux<Article> {
    val builder = ChangeStreamBuilder().apply(init)

    return changeStream(builder.getCollectionName(), builder.build(), Article::class.java)
        .mapNotNull { it.body }
}
