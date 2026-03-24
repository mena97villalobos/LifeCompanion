package com.mena97villalobos.remote.client

import com.mena97villalobos.remote.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val API_KEY_HEADER = "X-API-KEY"

fun provideHttpClient() = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            },
        )
    }

    install(Logging) {
        logger = Logger.ANDROID
        level = if (BuildKonfig.HTTP_LOGGING) LogLevel.ALL else LogLevel.NONE
        sanitizeHeader { header -> header == API_KEY_HEADER }
    }

    defaultRequest {
        url(BuildKonfig.EXCHANGE_API_ENDPOINT)

        header(
            API_KEY_HEADER,
            BuildKonfig.EXCHANGE_API_KEY,
        )

        contentType(ContentType.Application.Json)
    }
}
