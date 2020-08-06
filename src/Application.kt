package com.example

import com.example.vo.Product
import com.example.vo.SimpleJWT
import com.example.vo.User
import com.example.vo.users
import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.auth.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import io.ktor.auth.jwt.jwt
import io.ktor.jackson.*
import io.ktor.features.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

val eSUrl: String = System.getenv("ELASTICSEARCH_PRODUCT_ENDPOINT") ?: "http://localhost:9200/product"

suspend fun searchProduct(query: String, client: HttpClient): String {
    return client.get("${eSUrl}/_search") {
        body = TextContent(
            """
                    {
                        "query": {
                            "wildcard": {
                                "nom": "*${query}*"
                            }
                        }
                    }
                    """.trimIndent(),
            contentType = ContentType.Application.Json
        )
    }
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val simpleJwt = SimpleJWT("my-super-secret-for-jwt")

    install(Authentication) {
        jwt {
            verifier(simpleJwt.verifiyer)
            validate {
                UserIdPrincipal(it.payload.getClaim("name").asString())
            }
        }

        install(ContentNegotiation) {
            jackson {
                enable(SerializationFeature.INDENT_OUTPUT)
            }
        }

        val client = HttpClient(Apache) {
        }

        routing {

            post("/login-register") {
                val post = call.receive<LoginRegister>()
                val user = users.getOrPut(post.user) { User(post.user, post.password) }
                if (user.password != post.password) error("Invalid credentials")
                call.respond(mapOf("token" to simpleJwt.sign(user.name)))
            }

            get("/product/search") {
                call.respondText { Jackson.toJson(searchProduct("", HttpClient())) }
            }

            put ("/product/create") {
                    val post = call.receive<Product>()
                    client.post<String>("$eSUrl/_doc/14") {
                        body = TextContent(Jackson.toJson(post), contentType = ContentType.Application.Json)
                    }
                    call.respondText(Jackson.toJson(post), ContentType.Application.Json)
            }
        }
    }
}
