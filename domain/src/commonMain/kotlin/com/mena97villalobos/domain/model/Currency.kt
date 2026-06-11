package com.mena97villalobos.domain.model

/**
 * Currencies the user can pick during onboarding. [CRC] (Costa Rican colón) is the default,
 * matching the offline-first, Costa Rica-focused scope described in the BRD.
 */
enum class Currency(val code: String, val symbol: String) {
    CRC("CRC", "₡"),
    USD("USD", "$"),
    EUR("EUR", "€"),
    ;

    companion object {
        val Default: Currency = CRC

        /** Resolves an ISO code to a [Currency], falling back to [Default] for unknown codes. */
        fun fromCode(code: String?): Currency =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: Default
    }
}
