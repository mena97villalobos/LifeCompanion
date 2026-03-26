@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mena97villalobos.remote.services

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.posix.memcpy

class IosImageUriReader : ImageUriReader {
    override suspend fun read(imageUri: String): ByteArray = withContext(Dispatchers.Default) {
        val url: NSURL = when {
            imageUri.startsWith("http://") || imageUri.startsWith("https://") ->
                error("Remote URLs are not supported. Provide a local file URI/path: $imageUri")

            imageUri.startsWith("file://") ->
                requireNotNull(NSURL.URLWithString(imageUri)) { "Invalid image URI: $imageUri" }
            else -> NSURL.fileURLWithPath(imageUri)
        }
        val path = requireNotNull(url.path) { "No filesystem path for: $imageUri" }
        val data = NSFileManager.defaultManager.contentsAtPath(path)
            ?: error("Cannot read image at $imageUri")
        data.toByteArray()
    }
}

@Suppress("MagicNumber")
private fun NSData.toByteArray(): ByteArray {
    val len = length.toInt()
    if (len == 0) return ByteArray(0)
    val result = ByteArray(len)
    result.usePinned { pinned ->
        memcpy(pinned.addressOf(0), bytes, length)
    }
    return result
}
