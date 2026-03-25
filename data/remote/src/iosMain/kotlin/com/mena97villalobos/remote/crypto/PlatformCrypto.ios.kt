@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mena97villalobos.remote.crypto

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CC_SHA256
import platform.CoreCrypto.CCHmac
import platform.CoreCrypto.kCCHmacAlgSHA256

@Suppress("MagicNumber")
actual object PlatformCrypto {
    actual fun sha256(data: ByteArray): ByteArray {
        val digest = UByteArray(32)
        if (data.isEmpty()) {
            CC_SHA256(null, 0u, digest.refTo(0))
        } else {
            data.usePinned { pinned ->
                CC_SHA256(pinned.addressOf(0), data.size.toUInt(), digest.refTo(0))
            }
        }
        return digest.toByteArray()
    }

    actual fun hmacSha256(key: ByteArray, data: ByteArray): ByteArray {
        val mac = ByteArray(32)
        key.usePinned { kp ->
            data.usePinned { dp ->
                mac.usePinned { mp ->
                    CCHmac(
                        kCCHmacAlgSHA256,
                        kp.addressOf(0),
                        key.size.toULong(),
                        dp.addressOf(0),
                        data.size.toULong(),
                        mp.addressOf(0),
                    )
                }
            }
        }
        return mac
    }
}

@Suppress("MagicNumber")
private fun UByteArray.toByteArray(): ByteArray = ByteArray(size) { i -> this[i].toByte() }
