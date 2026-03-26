package com.mena97villalobos.local.di

import com.mena97villalobos.domain.usecases.AddWarrantyUseCase
import com.mena97villalobos.domain.usecases.DeleteWarrantyUseCase
import com.mena97villalobos.domain.usecases.GetWarrantiesUseCase
import com.mena97villalobos.domain.usecases.UpdateWarrantyUseCase
import com.mena97villalobos.domain.usecases.UploadWarrantyImageUseCase
import org.koin.dsl.module

/**
 * Warranty flows depend on [com.mena97villalobos.domain.repository.WarrantyRepository] (Room + MinIO),
 * so these factories live next to [localModule] / [iosLocalModule].
 */
val warrantyUseCaseModule = module {
    factory { GetWarrantiesUseCase(get()) }
    factory { AddWarrantyUseCase(get()) }
    factory { UpdateWarrantyUseCase(get()) }
    factory { DeleteWarrantyUseCase(get()) }
    factory { UploadWarrantyImageUseCase(get()) }
}
