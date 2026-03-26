package com.mena97villalobos.domain.services

/**
 * Abstraction over S3-compatible object storage (MinIO). Implementations differ by platform
 * (JVM MinIO SDK on Android, Ktor + SigV4 on iOS). The input must point to a local readable image
 * URI/path and the call may throw for validation, IO, auth, or transport failures.
 */
fun interface MinioService {
    /**
     * Uploads from a platform URI string and returns the generated object identifier.
     *
     * Not idempotent: each successful upload may produce a distinct object id.
     */
    suspend fun upload(imageUri: String): String
}
