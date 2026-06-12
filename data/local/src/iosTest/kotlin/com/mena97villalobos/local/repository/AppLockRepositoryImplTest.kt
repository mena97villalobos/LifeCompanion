package com.mena97villalobos.local.repository

import com.mena97villalobos.domain.security.PinHash
import com.mena97villalobos.domain.security.PinHasher
import com.mena97villalobos.local.security.StringBackedSecureStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AppLockRepositoryImplTest {

    private fun repo() = AppLockRepositoryImpl(InMemorySecureStorage(), FakePinHasher())

    @Test
    fun isPinSetReflectsSetAndClear() = runBlocking {
        val repository = repo()
        assertFalse(repository.isPinSet())

        repository.setPin("123456")
        assertTrue(repository.isPinSet())

        repository.clearPin()
        assertFalse(repository.isPinSet())
    }

    @Test
    fun verifyPinMatchesStoredHash() = runBlocking {
        val repository = repo()
        repository.setPin("123456")

        assertTrue(repository.verifyPin("123456"))
        assertFalse(repository.verifyPin("000000"))
    }

    @Test
    fun setPinNeverStoresPlaintext() = runBlocking {
        val storage = InMemorySecureStorage()
        val repository = AppLockRepositoryImpl(storage, FakePinHasher())

        repository.setPin("123456")

        // No stored value equals the raw PIN.
        assertTrue(storage.store.values.none { it == "123456" })
    }

    @Test
    fun failedAttemptsAccumulateAndReset() = runBlocking {
        val repository = repo()

        repository.recordFailedAttempt(nowMillis = 1_000L)
        repository.recordFailedAttempt(nowMillis = 2_000L)

        assertEquals(2, repository.getFailedAttempts())
        assertEquals(2_000L, repository.getLastFailedAtMillis())

        repository.resetFailedAttempts()
        assertEquals(0, repository.getFailedAttempts())
        assertNull(repository.getLastFailedAtMillis())
    }

    @Test
    fun settingPinResetsFailedAttempts() = runBlocking {
        val repository = repo()
        repository.recordFailedAttempt(nowMillis = 1_000L)

        repository.setPin("123456")

        assertEquals(0, repository.getFailedAttempts())
    }

    @Test
    fun biometricFlagPersistsAndEmits() = runBlocking {
        val repository = repo()
        assertFalse(repository.isBiometricEnabled())

        repository.setBiometricEnabled(true)

        assertTrue(repository.isBiometricEnabled())
        assertTrue(repository.observeBiometricEnabled().first())
    }

    @Test
    fun inactivityTimeoutDefaultsToFiveMinutes() = runBlocking {
        assertEquals(5 * 60_000L, repo().getInactivityTimeoutMillis())
    }

    /** In-memory [com.mena97villalobos.domain.security.SecureStorage] for tests. */
    private class InMemorySecureStorage : StringBackedSecureStorage() {
        val store = mutableMapOf<String, String>()

        override suspend fun putString(key: String, value: String) {
            store[key] = value
        }

        override suspend fun getString(key: String): String? = store[key]

        override suspend fun remove(key: String) {
            store.remove(key)
        }

        override suspend fun contains(key: String): Boolean = store.containsKey(key)
    }

    /** Deterministic stand-in for Argon2id: the "hash" is just a tagged copy of the PIN. */
    private class FakePinHasher : PinHasher {
        override suspend fun hash(pin: String): PinHash = PinHash(hash = "hashed:$pin", salt = "salt")

        override suspend fun verify(pin: String, expected: PinHash): Boolean =
            expected.hash == "hashed:$pin"
    }
}
