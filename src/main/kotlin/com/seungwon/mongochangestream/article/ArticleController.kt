package com.seungwon.mongochangestream.article

import com.seungwon.mongochangestream.StreamDataResponse
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.Duration

@RestController
@RequestMapping("/article")
internal class ArticleController(
    private val service: ArticleService
) {

    @PostMapping
    fun create(
        @RequestBody request: CreateArticleRequest
    ) = service.createNew(request.title, request.content)

    @PatchMapping("/{articleId}")
    fun edit(
        @PathVariable articleId: String,
        @RequestBody request: UpdateArticleRequest
    ) = service.edit(articleId, request.title, request.content)

    @GetMapping(
        value = ["/subscribe/{articleId}"],
        produces = [MediaType.TEXT_EVENT_STREAM_VALUE]
    )
    fun subscribe(
        @PathVariable articleId: String
    ): Flux<StreamDataResponse> = service.subscribe(articleId)

    @GetMapping(value = ["/test"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun test(): Flux<String> {
        return Flux.interval(Duration.ofSeconds(3))
            .map { count -> "Event: $count" }
            .take(30)
    }

}

internal data class CreateArticleRequest(
    val title: String,
    val content: String
)

internal data class UpdateArticleRequest(
    val title: String,
    val content: String
)