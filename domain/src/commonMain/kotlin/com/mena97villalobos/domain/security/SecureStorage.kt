package com.mena97villalobos.domain.security

/**
 * Platform-backed secure key/value store for small secrets (PIN hash, salt, lock preferences).
 *
 * Implementations must persist values in the OS-protected store:
 * - **Android**: `EncryptedSharedPreferences` (AES-256, keys in the Android Keystore).
 * - **iOS**: Keychain (`kSecClassGenericPassword`) via the `Security` framework.
 *
 * Values are plain strings; callers are responsible for only storing already-hashed/encoded
 * secrets (see [PinHasher]). Never store a plaintext PIN here.
 */
interface SecureStorage {
    suspend fun putString(key: String, value: String)

    suspend fun getString(key: String): String?

    suspend fun putInt(key: String, value: Int)

    suspend fun getInt(key: String): Int?

    suspend fun putLong(key: String, value: Long)

    suspend fun getLong(key: String): Long?

    suspend fun putBoolean(key: String, value: Boolean)

    suspend fun getBoolean(key: String): Boolean?

    suspend fun remove(key: String)

    suspend fun contains(key: String): Boolean
}
