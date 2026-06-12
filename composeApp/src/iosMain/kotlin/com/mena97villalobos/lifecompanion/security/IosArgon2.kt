package com.mena97villalobos.lifecompanion.security

import com.mena97villalobos.domain.security.PinHash
import com.mena97villalobos.domain.security.PinHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Bridge implemented in Swift (using the `Argon2Swift` SPM package) and passed into Kotlin at app
 * launch. Keeping the Argon2id implementation on the Swift side means the C library is delivered via
 * SPM rather than vendored into the KMP module.
 *
 * [hashEncoded] must return a PHC-formatted Argon2id string (`$argon2id$v=19$m=...`) embedding a
 * freshly generated random salt; [verify] re-runs Argon2id against that encoded string.
 */
interface IosArgon2 {
    fun hashEncoded(password: String): String

    fun verify(password: String, encoded: String): Boolean
}

/**
 * Set by the iOS app entry point before Koin starts, so the iOS [PinHasher] binding can resolve the
 * Swift-provided [IosArgon2] implementation.
 */
object IosArgon2Holder {
    var argon2: IosArgon2? = null
}

/**
 * [PinHasher] that delegates Argon2id work to the Swift [IosArgon2] bridge. The PHC-encoded string
 * embeds the salt, so [PinHash.salt] is left empty here.
 */
class IosPinHasher(
    private val argon2: IosArgon2,
) : PinHasher {

    override suspend fun hash(pin: String): PinHash = withContext(Dispatchers.Default) {
        PinHash(hash = argon2.hashEncoded(pin), salt = "")
    }

    override suspend fun verify(pin: String, expected: PinHash): Boolean = withContext(Dispatchers.Default) {
        argon2.verify(pin, expected.hash)
    }
}
