package com.example.controller

import com.example.services.ESRepository.searchProduct
import com.example.vo.Product
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post

private val eSUrl: String = System.getenv("ELASTICSEARCH_PRODUCT_ENDPOINT") ?: "http://localhost:9200/product"

fun Route.index() {

    val client = HttpClient(Apache) {
    }

    get("/product/search/{product}") {
        call.respondText(searchProduct(call.parameters["product"], client), ContentType.Application.Json)
    }

    post("/product/create") {
        val post = call.receive<Product>()
        client.post<String>("$eSUrl/_doc") {
            body =
                TextContent(jacksonObjectMapper().writeValueAsString(post), contentType = ContentType.Application.Json)
        }
        call.respondText(jacksonObjectMapper().writeValueAsString(post), ContentType.Application.Json)
    }
}