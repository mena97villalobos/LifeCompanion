package com.mena97villalobos.domain.repository

import com.mena97villalobos.domain.model.Warranty
import kotlinx.coroutines.flow.Flow

/**
 * Warranty persistence plus image upload orchestration.
 *
 * Implementations usually persist [Warranty] records locally and delegate [uploadImage] to an
 * object storage service. `imageUri` is platform-specific (`content://` on Android, `file://` or
 * absolute file path on iOS) and must reference a local readable image.
 */
interface WarrantyRepository {
    /** Inserts a new warranty record. */
    suspend fun insertWarranty(warranty: Warranty)

    /**
     * Updates an existing warranty.
     *
     * Implementations should fail fast when `warranty.id` is null to avoid accidental inserts.
     */
    suspend fun updateWarranty(warranty: Warranty)

    /** Deletes a warranty by local identifier. */
    suspend fun deleteWarranty(id: Long)

    /** Cold [Flow] of all warranties; implementations typically observe the local database. */
    fun getAllWarranties(): Flow<List<Warranty>>

    /**
     * Uploads an image and returns its stored object key.
     *
     * The operation is not idempotent: repeated calls can generate different object keys.
     * Implementations may throw for unreadable URIs, unsupported schemes, network failures,
     * and storage-level authorization errors.
     */
    suspend fun uploadImage(imageUri: String): String
}
