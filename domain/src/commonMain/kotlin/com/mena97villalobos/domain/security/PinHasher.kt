package com.mena97villalobos.domain.security

/**
 * Stored representation of a hashed PIN: the [hash] together with the [salt] used to derive it.
 * Both are Base64-encoded so they can live in [SecureStorage] as plain strings.
 */
data class PinHash(
    val hash: String,
    val salt: String,
)

/**
 * One-way PIN hashing using Argon2id, implemented per platform:
 * - **Android**: `argon2kt` (native Argon2).
 * - **iOS**: the reference Argon2 C implementation via cinterop.
 *
 * The PIN is never persisted in plaintext — only the [PinHash]. [verify] must be constant-time with
 * respect to the comparison so failures do not leak timing information.
 */
interface PinHasher {
    /** Derives an Argon2id [PinHash] for [pin], generating a fresh random [PinHash.salt]. */
    suspend fun hash(pin: String): PinHash

    /** Returns true when [pin] reproduces [expected] under the same salt and parameters. */
    suspend fun verify(pin: String, expected: PinHash): Boolean
}
