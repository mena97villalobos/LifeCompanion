package com.mena97villalobos.domain.repository

import android.net.Uri
import com.mena97villalobos.domain.model.Warranty
import kotlinx.coroutines.flow.Flow

interface WarrantyRepository {

    suspend fun insertWarranty(warranty: Warranty)

    suspend fun updateWarranty(warranty: Warranty)

    suspend fun deleteWarranty(id: Long)

    fun getAllWarranties(): Flow<List<Warranty>>

    suspend fun uploadImage(uri: Uri): String
}
