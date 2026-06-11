package com.mena97villalobos.domain.model

/**
 * Locales the app ships with. [ES_CR] (Spanish, Costa Rica) is the default; [EN] is the English
 * fallback. Stored on the [UserProfile] and applied at the UI layer.
 */
enum class AppLocale(val tag: String) {
    ES_CR("es-CR"),
    EN("en"),
    ;

    companion object {
        val Default: AppLocale = ES_CR

        /** Resolves a BCP-47 tag to an [AppLocale], falling back to [Default] for unknown tags. */
        fun fromTag(tag: String?): AppLocale =
            entries.firstOrNull { it.tag.equals(tag, ignoreCase = true) } ?: Default
    }
}
