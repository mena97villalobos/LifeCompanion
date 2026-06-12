package com.mena97villalobos.domain.security

import com.mena97villalobos.domain.repository.PassphraseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** In-memory [PassphraseRepository] for [PassphraseManagerTest]; comparison is plaintext for the test. */
class FakePassphraseRepository : PassphraseRepository {
    var storedPassphrase: String? = null
    var failedAttempts: Int = 0
    var lastFailedAt: Long? = null
    private val enabled = MutableStateFlow(false)

    override suspend fun isEnabled(): Boolean = storedPassphrase != null

    override fun observeEnabled(): Flow<Boolean> = enabled.asStateFlow()

    override suspend fun setPassphrase(passphrase: String) {
        storedPassphrase = passphrase
        failedAttempts = 0
        lastFailedAt = null
        enabled.value = true
    }

    override suspend fun verifyPassphrase(passphrase: String): Boolean = storedPassphrase == passphrase

    override suspend fun clearPassphrase() {
        storedPassphrase = null
        failedAttempts = 0
        lastFailedAt = null
        enabled.value = false
    }

    override suspend fun getFailedAttempts(): Int = failedAttempts

    override suspend fun getLastFailedAtMillis(): Long? = lastFailedAt

    override suspend fun recordFailedAttempt(nowMillis: Long) {
        failedAttempts += 1
        lastFailedAt = nowMillis
    }

    override suspend fun resetFailedAttempts() {
        failedAttempts = 0
        lastFailedAt = null
    }
}
