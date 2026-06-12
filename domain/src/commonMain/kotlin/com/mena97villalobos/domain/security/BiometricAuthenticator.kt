package com.mena97villalobos.domain.security

/** Whether biometric authentication can be used right now on this device. */
enum class BiometricAvailability {
    /** Hardware present, enrolled, and ready. */
    AVAILABLE,

    /** Hardware present but the user has not enrolled any biometric. */
    NOT_ENROLLED,

    /** No biometric hardware, or it is permanently unavailable. */
    UNAVAILABLE,
}

/** Outcome of a biometric prompt. */
sealed interface BiometricResult {
    data object Success : BiometricResult

    /** The user cancelled or dismissed the prompt. */
    data object Cancelled : BiometricResult

    /** Authentication failed or errored; [message] is a human-readable reason when available. */
    data class Failed(val message: String?) : BiometricResult
}

/**
 * Platform biometric prompt:
 * - **Android**: `androidx.biometric.BiometricPrompt` (fingerprint / face).
 * - **iOS**: `LocalAuthentication` (`LAContext`, Face ID / Touch ID).
 *
 * Desktop has no standardized biometric API, so the default desktop implementation reports
 * [BiometricAvailability.UNAVAILABLE] and PIN is used instead. (Desktop is out of scope for V1; see
 * the issue tracker.)
 */
interface BiometricAuthenticator {
    fun availability(): BiometricAvailability

    /**
     * Shows the system biometric prompt and suspends until the user responds.
     *
     * @param title prompt title shown to the user.
     * @param subtitle optional secondary line.
     */
    suspend fun authenticate(title: String, subtitle: String? = null): BiometricResult
}
