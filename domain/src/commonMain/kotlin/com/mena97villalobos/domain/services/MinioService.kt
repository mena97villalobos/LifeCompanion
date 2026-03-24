package com.mena97villalobos.domain.services

fun interface MinioService {
    suspend fun upload(imageUri: String): String
}
