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

class PassphraseRepositoryImplTest {

    private val passphrase = "correct horse battery"

    private fun repo() = PassphraseRepositoryImpl(InMemorySecureStorage(), FakePinHasher())

    @Test
    fun isEnabledReflectsSetAndClear() = runBlocking {
        val repository = repo()
        assertFalse(repository.isEnabled())

        repository.setPassphrase(passphrase)
        assertTrue(repository.isEnabled())

        repository.clearPassphrase()
        assertFalse(repository.isEnabled())
    }

    @Test
    fun verifyMatchesStoredHash() = runBlocking {
        val repository = repo()
        repository.setPassphrase(passphrase)

        assertTrue(repository.verifyPassphrase(passphrase))
        assertFalse(repository.verifyPassphrase("something else entirely"))
    }

    @Test
    fun setNeverStoresPlaintext() = runBlocking {
        val storage = InMemorySecureStorage()
        val repository = PassphraseRepositoryImpl(storage, FakePinHasher())

        repository.setPassphrase(passphrase)

        assertTrue(storage.store.values.none { it == passphrase })
    }

    @Test
    fun observeEnabledEmitsOnSetAndClear() = runBlocking {
        val repository = repo()
        repository.setPassphrase(passphrase)
        assertTrue(repository.observeEnabled().first())

        repository.clearPassphrase()
        assertFalse(repository.observeEnabled().first())
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
    fun settingPassphraseResetsFailedAttempts() = runBlocking {
        val repository = repo()
        repository.recordFailedAttempt(nowMillis = 1_000L)

        repository.setPassphrase(passphrase)

        assertEquals(0, repository.getFailedAttempts())
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

    /** Deterministic stand-in for Argon2id: the "hash" is just a tagged copy of the input. */
    private class FakePinHasher : PinHasher {
        override suspend fun hash(pin: String): PinHash = PinHash(hash = "hashed:$pin", salt = "salt")

        override suspend fun verify(pin: String, expected: PinHash): Boolean =
            expected.hash == "hashed:$pin"
    }
}
