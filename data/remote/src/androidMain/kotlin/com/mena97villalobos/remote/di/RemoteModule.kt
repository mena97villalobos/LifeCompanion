package com.mena97villalobos.remote.di

import com.mena97villalobos.domain.services.MinioService
import com.mena97villalobos.remote.BuildKonfig
import com.mena97villalobos.remote.services.MinioServiceImpl
import io.minio.MinioClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/** JVM MinIO client and [com.mena97villalobos.domain.services.MinioService] for Android. */
internal val minioJvmModule = module {
    single<MinioClient> {
        MinioClient.builder()
            .endpoint(BuildKonfig.MINIO_ENDPOINT)
            .credentials(
                BuildKonfig.MINIO_ENDPOINT_ACCESS_KEY,
                BuildKonfig.MINIO_ENDPOINT_SECRET_KEY,
            )
            .build()
    }

    single<MinioService> {
        MinioServiceImpl(
            dispatcherService = get(),
            context = androidContext(),
            minioClient = get(),
            bucketName = BuildKonfig.MINIO_BUCKET_NAME,
        )
    }
}

/**
 * Full Android remote stack: [remoteCoreModule] (HTTP + exchange rates) plus [minioJvmModule].
 * Build-time MinIO and API settings come from [com.mena97villalobos.remote.BuildKonfig].
 */
val remoteModule = module {
    includes(remoteCoreModule)
    includes(minioJvmModule)
}
