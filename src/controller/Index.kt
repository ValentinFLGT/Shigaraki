package com.example.controller

import com.example.services.Jackson
import com.example.eSUrl
import com.example.searchProduct
import com.example.vo.Product
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

fun Route.index() {

    val client = HttpClient(Apache) {
    }

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