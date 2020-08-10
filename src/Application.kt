package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.controller.index
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.jackson.jackson
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

val eSUrl: String = System.getenv("ELASTICSEARCH_PRODUCT_ENDPOINT") ?: "http://localhost:9200/"

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

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val jwtIssuer = environment.config.property("ktor.jwt.domain").getString()
    val jwtAudience = environment.config.property("ktor.jwt.audience").getString()
    val jwtRealm = environment.config.property("ktor.jwt.realm").getString()

    val algorithm = Algorithm.HMAC256("test")
    fun makeJwtVerifier(issuer: String, audience: String): JWTVerifier = JWT
        .require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(Authentication) {
        jwt {
            realm = jwtRealm
            verifier(makeJwtVerifier(jwtIssuer, jwtIssuer))
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }

    routing {
        index()
    }
}

