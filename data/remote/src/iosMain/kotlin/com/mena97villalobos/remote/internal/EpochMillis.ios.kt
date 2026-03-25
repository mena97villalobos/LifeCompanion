@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mena97villalobos.remote.internal

import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.gettimeofday
import platform.posix.timeval

@Suppress("MagicNumber")
internal actual fun epochMillis(): Long = memScoped {
    val tv = alloc<timeval>()
    gettimeofday(tv.ptr, null)
    tv.tv_sec * 1000L + tv.tv_usec / 1000L
}
