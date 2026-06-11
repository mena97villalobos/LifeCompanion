package com.mena97villalobos.local.security

import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode
import com.mena97villalobos.domain.security.PinHash
import com.mena97villalobos.domain.security.PinHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Argon2id PIN hashing on Android via the native `argon2kt` library. The PHC-encoded string (which
 * embeds the salt and cost parameters) is stored in [PinHash.hash]; verification re-runs Argon2id
 * over the candidate PIN. The plaintext PIN never leaves this function.
 */
@OptIn(ExperimentalEncodingApi::class)
class AndroidArgon2PinHasher(
    private val argon2: Argon2Kt = Argon2Kt(),
) : PinHasher {

    override suspend fun hash(pin: String): PinHash = withContext(Dispatchers.Default) {
        val salt = ByteArray(SALT_LENGTH).also { SecureRandom().nextBytes(it) }
        val result = argon2.hash(
            mode = Argon2Mode.ARGON2_ID,
            password = pin.encodeToByteArray(),
            salt = salt,
            tCostInIterations = ITERATIONS,
            mCostInKibibyte = MEMORY_KIB,
            parallelism = PARALLELISM,
            hashLengthInBytes = HASH_LENGTH,
        )
        PinHash(
            hash = result.encodedOutputAsString(),
            salt = Base64.encode(salt),
        )
    }

    override suspend fun verify(pin: String, expected: PinHash): Boolean = withContext(Dispatchers.Default) {
        argon2.verify(
            mode = Argon2Mode.ARGON2_ID,
            encoded = expected.hash,
            password = pin.encodeToByteArray(),
        )
    }

    private companion object {
        const val SALT_LENGTH = 16
        const val HASH_LENGTH = 32
        const val ITERATIONS = 3
        const val MEMORY_KIB = 65_536
        const val PARALLELISM = 1
    }
}
