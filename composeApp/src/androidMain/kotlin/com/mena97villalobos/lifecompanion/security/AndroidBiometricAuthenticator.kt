package com.mena97villalobos.lifecompanion.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.mena97villalobos.domain.security.BiometricAuthenticator
import com.mena97villalobos.domain.security.BiometricAvailability
import com.mena97villalobos.domain.security.BiometricResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

/**
 * [BiometricAuthenticator] backed by `androidx.biometric.BiometricPrompt`. The prompt requires a
 * `FragmentActivity`, obtained from [AndroidActivityProvider] at authentication time.
 */
class AndroidBiometricAuthenticator(
    private val context: Context,
) : BiometricAuthenticator {

    override fun availability(): BiometricAvailability =
        when (BiometricManager.from(context).canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NOT_ENROLLED
            else -> BiometricAvailability.UNAVAILABLE
        }

    override suspend fun authenticate(title: String, subtitle: String?): BiometricResult {
        val activity = AndroidActivityProvider.current
            ?: return BiometricResult.Failed("No foreground activity for biometric prompt")

        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                val callback = object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        if (continuation.isActive) continuation.resume(BiometricResult.Success)
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        if (!continuation.isActive) return
                        val result = when (errorCode) {
                            BiometricPrompt.ERROR_USER_CANCELED,
                            BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                            BiometricPrompt.ERROR_CANCELED,
                            -> BiometricResult.Cancelled

                            else -> BiometricResult.Failed(errString.toString())
                        }
                        continuation.resume(result)
                    }

                    // A single non-matching attempt; the system prompt stays up, so do not resume here.
                    override fun onAuthenticationFailed() = Unit
                }

                val prompt = BiometricPrompt(activity, ContextCompat.getMainExecutor(activity), callback)
                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .apply { subtitle?.let { setSubtitle(it) } }
                    .setNegativeButtonText("Use PIN")
                    .setAllowedAuthenticators(BIOMETRIC_STRONG)
                    .build()

                prompt.authenticate(promptInfo)
                continuation.invokeOnCancellation { prompt.cancelAuthentication() }
            }
        }
    }
}
