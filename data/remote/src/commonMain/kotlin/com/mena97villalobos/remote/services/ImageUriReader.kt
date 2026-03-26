package com.mena97villalobos.remote.services

/**
 * Reads image bytes from a local platform URI/path string.
 *
 * Supported URI schemes are implementation-specific. Callers should pass local image sources only.
 */
fun interface ImageUriReader {
    /** Returns raw bytes for the given local image URI/path; throws when unreadable/unsupported. */
    suspend fun read(imageUri: String): ByteArray
}
