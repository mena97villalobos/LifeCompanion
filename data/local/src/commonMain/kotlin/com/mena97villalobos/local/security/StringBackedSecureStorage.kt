package com.mena97villalobos.local.security

import com.mena97villalobos.domain.security.SecureStorage

/**
 * Implements the typed [SecureStorage] accessors on top of a string-only secure backend, so each
 * platform only needs to provide encrypted string get/put/remove. Numbers and booleans are stored
 * as their string form.
 */
abstract class StringBackedSecureStorage : SecureStorage {

    override suspend fun putInt(key: String, value: Int) = putString(key, value.toString())

    override suspend fun getInt(key: String): Int? = getString(key)?.toIntOrNull()

    override suspend fun putLong(key: String, value: Long) = putString(key, value.toString())

    override suspend fun getLong(key: String): Long? = getString(key)?.toLongOrNull()

    override suspend fun putBoolean(key: String, value: Boolean) = putString(key, value.toString())

    override suspend fun getBoolean(key: String): Boolean? = getString(key)?.toBooleanStrictOrNull()
}
