@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package com.mena97villalobos.lifecompanion.security

import com.mena97villalobos.domain.security.BiometricAuthenticator
import com.mena97villalobos.domain.security.BiometricAvailability
import com.mena97villalobos.domain.security.BiometricResult
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAErrorBiometryNotEnrolled
import platform.LocalAuthentication.LAErrorAppCancel
import platform.LocalAuthentication.LAErrorSystemCancel
import platform.LocalAuthentication.LAErrorUserCancel
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import kotlin.coroutines.resume

/**
 * [BiometricAuthenticator] backed by `LocalAuthentication` (`LAContext`, Face ID / Touch ID). A
 * fresh [LAContext] is created per evaluation, as recommended by Apple.
 */
class IosBiometricAuthenticator : BiometricAuthenticator {

    override fun availability(): BiometricAvailability = memScoped {
        val error = alloc<ObjCObjectVar<NSError?>>()
        val canEvaluate = LAContext().canEvaluatePolicy(
            LAPolicyDeviceOwnerAuthenticationWithBiometrics,
            error.ptr,
        )
        when {
            canEvaluate -> BiometricAvailability.AVAILABLE
            error.value?.code == LAErrorBiometryNotEnrolled -> BiometricAvailability.NOT_ENROLLED
            else -> BiometricAvailability.UNAVAILABLE
        }
    }

    override suspend fun authenticate(title: String, subtitle: String?): BiometricResult =
        suspendCancellableCoroutine { continuation ->
            val context = LAContext()
            val reason = subtitle?.let { "$title\n$it" } ?: title
            context.evaluatePolicy(
                LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                localizedReason = reason,
            ) { success, error ->
                if (!continuation.isActive) return@evaluatePolicy
                val result = when {
                    success -> BiometricResult.Success
                    error.isCancellation() -> BiometricResult.Cancelled
                    else -> BiometricResult.Failed(error?.localizedDescription)
                }
                continuation.resume(result)
            }
        }

    private fun NSError?.isCancellation(): Boolean {
        val code = this?.code ?: return false
        return code == LAErrorUserCancel || code == LAErrorSystemCancel || code == LAErrorAppCancel
    }
}
