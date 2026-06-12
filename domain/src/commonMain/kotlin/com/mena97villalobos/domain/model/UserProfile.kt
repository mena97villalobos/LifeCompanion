package com.mena97villalobos.domain.model

/**
 * Device-local user profile created during first-launch onboarding.
 *
 * LifeCompanion is offline-first with no backend in V1, so there is no email/account: the profile
 * lives only in the local Room database. A single profile is expected per device, pinned to
 * [SINGLETON_ID].
 */
data class UserProfile(
    val id: Long = SINGLETON_ID,
    val displayName: String,
    val currency: Currency = Currency.Default,
    val locale: AppLocale = AppLocale.Default,
) {
    companion object {
        /** The only profile row; the app supports a single local profile per device. */
        const val SINGLETON_ID: Long = 1L
    }
}
