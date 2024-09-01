package com.seungwon.mongochangestream

data class StreamDataResponse(
    val operationType: String,
    val fullDocument: Any?,
)
