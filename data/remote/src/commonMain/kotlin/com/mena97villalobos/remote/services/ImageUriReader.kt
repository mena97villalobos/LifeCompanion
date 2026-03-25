package com.mena97villalobos.remote.services

fun interface ImageUriReader {
    suspend fun read(imageUri: String): ByteArray
}
