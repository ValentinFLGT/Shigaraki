package com.example.services

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent

object ESRepository {

    private val eSUrl: String = System.getenv("ELASTICSEARCH_PRODUCT_ENDPOINT") ?: "http://localhost:9200/product"

    suspend fun searchProduct(product: String?, client: HttpClient): String {
        return client.get("${eSUrl}/_search") {
            body = TextContent(
                """{
                    "query": {
                    "bool": {
                    "must": [
                    { "match": { "name": "$product" } }
                    ]
                    }}}
                """,
                contentType = ContentType.Application.Json
            )
        }
    }
}