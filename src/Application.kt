package com.example

import com.example.controller.index
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.jackson.jackson
import io.ktor.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

val eSUrl: String = System.getenv("ELASTICSEARCH_PRODUCT_ENDPOINT") ?: "http://localhost:9200/product"

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

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        index()
    }
}

