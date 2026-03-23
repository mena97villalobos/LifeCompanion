package com.mena97villalobos.remote.services

import android.content.Context
import android.net.Uri
import com.mena97villalobos.domain.services.DispatcherService
import com.mena97villalobos.domain.services.MinioService
import io.minio.MinioClient
import io.minio.PutObjectArgs
import kotlinx.coroutines.withContext
import java.util.UUID

private const val MIME_TYPE = "image/jpeg"
private const val FILE_SIZE_PART = 10485760L

class MinioServiceImpl(
    private val dispatcherService: DispatcherService,
    private val context: Context,
    private val minioClient: MinioClient,
    private val bucketName: String,
) : MinioService {

    override suspend fun upload(uri: Uri): String = withContext(dispatcherService.io) {
        val objectId = UUID.randomUUID().toString()

        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Cannot open file")

        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`(objectId)
                .stream(inputStream, -1, FILE_SIZE_PART)
                .contentType(MIME_TYPE)
                .build(),
        )

        objectId
    }
}
