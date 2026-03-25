package com.mena97villalobos.remote.services

import com.mena97villalobos.domain.services.DispatcherService
import com.mena97villalobos.domain.services.MinioService
import com.mena97villalobos.remote.BuildKonfig
import com.mena97villalobos.remote.crypto.PlatformCrypto
import com.mena97villalobos.remote.crypto.toHexLower
import com.mena97villalobos.remote.internal.epochMillis
import com.mena97villalobos.remote.minio.AwsSigV4Signer
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.request.headers
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

private const val MIME_JPEG = "image/jpeg"

private data class ParsedMinioEndpoint(
    val urlPrefix: String,
    val hostHeader: String,
)

class MinioS3KtorService(
    engine: HttpClientEngine,
    private val dispatcherService: DispatcherService,
    private val imageUriReader: ImageUriReader,
    private val region: String = "us-east-1",
) : MinioService {

    private val client = HttpClient(engine) {
        expectSuccess = true
    }

    override suspend fun upload(imageUri: String): String = withContext(dispatcherService.io) {
        val bytes = imageUriReader.read(imageUri)
        val objectId = randomObjectId()
        val parsed = parseMinioEndpoint()
        val bucket = BuildKonfig.MINIO_BUCKET_NAME
        val canonicalUri = "/$bucket/$objectId"
        val url = "${parsed.urlPrefix}$canonicalUri"
        val bodyHash = PlatformCrypto.sha256(bytes).toHexLower()
        val (amzDate, dateStamp) = amzTimestamps()
        val auth = AwsSigV4Signer.authorizationHeaderForPut(
            method = "PUT",
            canonicalUri = canonicalUri,
            hostHeader = parsed.hostHeader,
            bodyHashHex = bodyHash,
            amzDate = amzDate,
            dateStamp = dateStamp,
            region = region,
            accessKey = BuildKonfig.MINIO_ENDPOINT_ACCESS_KEY,
            secretKey = BuildKonfig.MINIO_ENDPOINT_SECRET_KEY,
        )
        client.put(url) {
            headers {
                append("x-amz-date", amzDate)
                append("x-amz-content-sha256", bodyHash)
                append(HttpHeaders.Authorization, auth)
                append(HttpHeaders.ContentType, MIME_JPEG)
            }
            setBody(bytes)
        }
        objectId
    }
}

private fun parseMinioEndpoint(): ParsedMinioEndpoint {
    val raw = BuildKonfig.MINIO_ENDPOINT.trim()
    val withScheme = if (!raw.contains("://")) "http://$raw" else raw
    val withoutScheme = withScheme.substringAfter("://")
    val hostPort = withoutScheme.substringBefore('/').trimEnd('/')
    return ParsedMinioEndpoint(
        urlPrefix = withScheme.trimEnd('/'),
        hostHeader = hostPort,
    )
}

@Suppress("DEPRECATION")
private fun amzTimestamps(): Pair<String, String> {
    val ldt = Instant.fromEpochMilliseconds(epochMillis()).toLocalDateTime(TimeZone.UTC)
    val y = ldt.year.toString().padStart(4, '0')
    val mo = ldt.monthNumber.toString().padStart(2, '0')
    val d = ldt.day.toString().padStart(2, '0')
    val h = ldt.hour.toString().padStart(2, '0')
    val mi = ldt.minute.toString().padStart(2, '0')
    val s = ldt.second.toString().padStart(2, '0')
    val dateStamp = "$y$mo$d"
    val amzDate = "${dateStamp}T$h$mi${s}Z"
    return amzDate to dateStamp
}

private fun randomObjectId(): String = buildString {
    repeat(32) {
        append("0123456789abcdef"[Random.nextInt(16)])
    }
}
