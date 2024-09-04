package com.seungwon.mongochangestream.article

import com.mongodb.client.model.changestream.FullDocument
import com.mongodb.client.model.changestream.OperationType
import com.seungwon.mongochangestream.StreamDataResponse
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ChangeStreamEvent
import org.springframework.data.mongodb.core.ChangeStreamOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
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
            id = null,
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

    fun subscribe(articleId: String): Flux<StreamDataResponse> {
        return mongoTemplate.changeStream(
                "articles",
                ChangeStreamOptions.builder()
                    .filter(Aggregation.newAggregation(
                        Aggregation.match(
                            Criteria.where("operationType").`is`(OperationType.REPLACE.value)
                                .orOperator(
                                    listOf(
                                        Criteria.where("fullDocument._id").`is`(articleId),
                                        Criteria.where("documentKey._id").`is`(ObjectId(articleId))
                                    )
                                )
                        )
                    ))
                    .fullDocumentLookup(FullDocument.DEFAULT)
                    .build(),
                Article::class.java)
            .map { extractChangedFields(it) }
    }

    private fun extractChangedFields(event: ChangeStreamEvent<Article>): StreamDataResponse {
        val document = event.raw
        val operationType = document?.operationType ?: OperationType.REPLACE

        return StreamDataResponse(
            operationType = operationType.value,
            fullDocument = when (operationType) {
                OperationType.REPLACE -> event.body
                else -> null
            }
        )
    }
}
