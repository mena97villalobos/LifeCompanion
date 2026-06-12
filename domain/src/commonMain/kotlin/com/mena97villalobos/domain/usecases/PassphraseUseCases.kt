package com.mena97villalobos.domain.usecases

import com.mena97villalobos.domain.repository.PassphraseRepository
import com.mena97villalobos.domain.security.PassphraseManager
import com.mena97villalobos.domain.security.PassphraseOutcome
import kotlinx.coroutines.flow.Flow

/** Rules for the sensitive-operations passphrase (issue #9). */
object Passphrase {
    /** Minimum length, deliberately longer than the 6-digit PIN for extra protection. */
    const val MIN_LENGTH = 12

    fun isValid(passphrase: String): Boolean = passphrase.length >= MIN_LENGTH
}

/** Observes whether the passphrase requirement is enabled, for the settings toggle. */
class ObservePassphraseEnabledUseCase(
    private val repository: PassphraseRepository,
) {
    operator fun invoke(): Flow<Boolean> = repository.observeEnabled()
}

/** True when the passphrase requirement is currently enabled. */
class IsPassphraseEnabledUseCase(
    private val repository: PassphraseRepository,
) {
    suspend operator fun invoke(): Boolean = repository.isEnabled()
}

/**
 * Enables the requirement by setting a passphrase. Validates the [MIN_LENGTH][Passphrase.MIN_LENGTH]
 * rule; the repository hashes it with Argon2id before storage.
 */
class EnablePassphraseUseCase(
    private val repository: PassphraseRepository,
) {
    suspend operator fun invoke(passphrase: String) {
        require(Passphrase.isValid(passphrase)) {
            "Passphrase must be at least ${Passphrase.MIN_LENGTH} characters"
        }
        repository.setPassphrase(passphrase)
    }
}

/**
 * Disables the requirement. This is itself a sensitive operation, so it requires confirming the
 * [current] passphrase (with backoff via [PassphraseManager]); the stored hash is cleared only on
 * [PassphraseOutcome.Success].
 */
class DisablePassphraseUseCase(
    private val manager: PassphraseManager,
    private val repository: PassphraseRepository,
) {
    suspend operator fun invoke(current: String): PassphraseOutcome {
        val outcome = manager.submit(current)
        if (outcome is PassphraseOutcome.Success) repository.clearPassphrase()
        return outcome
    }
}

/**
 * Changes the passphrase: confirms the [current] passphrase (with backoff) and, on success, stores
 * the validated [new] one. Returns the confirmation [PassphraseOutcome]; throws if [new] is invalid.
 */
class ChangePassphraseUseCase(
    private val manager: PassphraseManager,
    private val repository: PassphraseRepository,
) {
    suspend operator fun invoke(current: String, new: String): PassphraseOutcome {
        require(Passphrase.isValid(new)) {
            "Passphrase must be at least ${Passphrase.MIN_LENGTH} characters"
        }
        val outcome = manager.submit(current)
        if (outcome is PassphraseOutcome.Success) repository.setPassphrase(new)
        return outcome
    }
}
