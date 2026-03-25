package com.mena97villalobos.remote.crypto

expect object PlatformCrypto {
    fun sha256(data: ByteArray): ByteArray

    fun hmacSha256(key: ByteArray, data: ByteArray): ByteArray
}

fun ByteArray.toHexLower(): String = joinToString("") { b ->
    val u = b.toInt() and 0xFF
    u.toString(16).padStart(2, '0')
}
