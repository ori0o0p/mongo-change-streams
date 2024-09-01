package com.seungwon.mongochangestream.article

import com.mongodb.client.model.changestream.FullDocument
import com.mongodb.client.model.changestream.OperationType
import com.seungwon.mongochangestream.StreamDataResponse
import org.springframework.data.mongodb.core.ChangeStreamEvent
import org.springframework.data.mongodb.core.ChangeStreamOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
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
            }
            .flatMap { repository.save(it) }
            .then()

    fun subscribe(articleId: String): Flux<StreamDataResponse> {
        return mongoTemplate.changeStream(
            "articles",
            ChangeStreamOptions.builder()
                .fullDocumentLookup(FullDocument.UPDATE_LOOKUP)
                .build(),
            Article::class.java)
            .filter { it.raw?.documentKey?.get("_id")?.asObjectId()?.value?.toString() == (articleId) }
            .map { extractChangedFields(it) }
    }

    private fun extractChangedFields(event: ChangeStreamEvent<Article>): StreamDataResponse {
        val document = event.raw
        val operationType = document?.operationType ?: OperationType.UPDATE

        return StreamDataResponse(
            operationType = operationType.value,
            fullDocument = when (operationType) {
                OperationType.UPDATE, OperationType.REPLACE -> event.body
                OperationType.DELETE -> null
                else -> null
            }
        )
    }
}
