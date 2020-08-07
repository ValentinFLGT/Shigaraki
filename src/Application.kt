package com.example

import com.example.vo.Product
import io.ktor.application.*
import io.ktor.routing.*
import com.fasterxml.jackson.databind.*
import io.ktor.jackson.*
import io.ktor.features.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.request.receive
import io.ktor.response.respondText

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

    val client = HttpClient(Apache) {
    }

    routing {

        get("/product/search/{product}") {
            call.respondText(searchProduct(call.parameters["product"], client), ContentType.Application.Json)
        }

        post("/product/create") {
            val post = call.receive<Product>()
            client.post<String>("$eSUrl/_doc") {
                body = TextContent(Jackson.toJson(post), contentType = ContentType.Application.Json)
            }
            call.respondText(Jackson.toJson(post), ContentType.Application.Json)
        }
    }
}

