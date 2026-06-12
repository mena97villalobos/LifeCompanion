@file:OptIn(ExperimentalForeignApi::class)

package com.mena97villalobos.local.security

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDataCreate
import platform.CoreFoundation.CFDataGetBytePtr
import platform.CoreFoundation.CFDataGetLength
import platform.CoreFoundation.CFDataRef
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringCreateWithCString
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFStringEncodingUTF8
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

/**
 * [com.mena97villalobos.domain.security.SecureStorage] backed by the iOS Keychain
 * (`kSecClassGenericPassword`). Items use `kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly` so
 * secrets stay on-device and are unavailable until the user first unlocks the phone after boot.
 */
class IosSecureStorage(
    private val service: String = DEFAULT_SERVICE,
) : StringBackedSecureStorage() {

    override suspend fun putString(key: String, value: String) {
        remove(key)
        val bytes = value.encodeToByteArray()
        val cfService = cfString(service)
        val cfAccount = cfString(key)
        val cfData = bytes.toCFData()
        val query = CFDictionaryCreateMutable(
            kCFAllocatorDefault,
            0,
            kCFTypeDictionaryKeyCallBacks.ptr,
            kCFTypeDictionaryValueCallBacks.ptr,
        )
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrService, cfService)
        CFDictionaryAddValue(query, kSecAttrAccount, cfAccount)
        CFDictionaryAddValue(query, kSecValueData, cfData)
        CFDictionaryAddValue(query, kSecAttrAccessible, kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly)
        SecItemAdd(query, null)
        CFRelease(query)
        CFRelease(cfService)
        CFRelease(cfAccount)
        cfData?.let { CFRelease(it) }
    }

    override suspend fun getString(key: String): String? = memScoped {
        val cfService = cfString(service)
        val cfAccount = cfString(key)
        val query = CFDictionaryCreateMutable(
            kCFAllocatorDefault,
            0,
            kCFTypeDictionaryKeyCallBacks.ptr,
            kCFTypeDictionaryValueCallBacks.ptr,
        )
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrService, cfService)
        CFDictionaryAddValue(query, kSecAttrAccount, cfAccount)
        CFDictionaryAddValue(query, kSecReturnData, kCFBooleanTrue)
        CFDictionaryAddValue(query, kSecMatchLimit, kSecMatchLimitOne)

        val result = alloc<CFTypeRefVar>()
        val status = SecItemCopyMatching(query, result.ptr)
        CFRelease(query)
        CFRelease(cfService)
        CFRelease(cfAccount)

        if (status != errSecSuccess) return@memScoped null
        val data = result.value as? CFDataRef ?: return@memScoped null
        val decoded = data.toByteArray().decodeToString()
        CFRelease(data)
        decoded
    }

    override suspend fun remove(key: String) {
        val cfService = cfString(service)
        val cfAccount = cfString(key)
        val query = CFDictionaryCreateMutable(
            kCFAllocatorDefault,
            0,
            kCFTypeDictionaryKeyCallBacks.ptr,
            kCFTypeDictionaryValueCallBacks.ptr,
        )
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrService, cfService)
        CFDictionaryAddValue(query, kSecAttrAccount, cfAccount)
        SecItemDelete(query)
        CFRelease(query)
        CFRelease(cfService)
        CFRelease(cfAccount)
    }

    override suspend fun contains(key: String): Boolean = getString(key) != null

    private fun cfString(value: String): CFStringRef? =
        CFStringCreateWithCString(kCFAllocatorDefault, value, kCFStringEncodingUTF8)

    private fun ByteArray.toCFData(): CFDataRef? {
        if (isEmpty()) return CFDataCreate(kCFAllocatorDefault, null, 0)
        return usePinned { pinned ->
            CFDataCreate(
                kCFAllocatorDefault,
                pinned.addressOf(0).reinterpret(),
                size.convert(),
            )
        }
    }

    private fun CFDataRef.toByteArray(): ByteArray {
        val length = CFDataGetLength(this).toInt()
        if (length == 0) return ByteArray(0)
        val bytePtr = CFDataGetBytePtr(this) ?: return ByteArray(0)
        return ByteArray(length) { bytePtr[it].toByte() }
    }

    private companion object {
        const val DEFAULT_SERVICE = "com.mena97villalobos.lifecompanion.applock"
    }
}
