package com.mena97villalobos.domain.services

import android.net.Uri

fun interface MinioService {
    suspend fun upload(uri: Uri): String
}
