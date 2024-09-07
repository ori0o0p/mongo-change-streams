package com.seungwon.mongochangestream.mongo

import com.mongodb.client.model.changestream.OperationType
import org.springframework.data.mongodb.core.ChangeStreamOptions
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria

class ChangeStreamBuilder {
    private var collection: String = ""
    private val filters = mutableListOf<Criteria>()
    private var operationType: OperationType? = null

    fun collectionName(name: String) = apply { collection = name }
    fun filterBy(criteria: Criteria) = apply { filters.add(criteria) }
    fun operationType(type: OperationType) = apply { operationType = type }

    fun build(): ChangeStreamOptions {
        val criteria = Criteria().apply {
            operationType.let { and("operationType").`is`(it) }

            if (filters.isNotEmpty()) {
                orOperator(filters)
            }
        }

        return ChangeStreamOptions.builder()
            .filter(Aggregation.newAggregation(Aggregation.match(criteria)))
            .build()
    }

    fun getCollectionName() = collection
}