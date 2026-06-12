package com.mena97villalobos.domain.security

/**
 * Lockout schedule applied after repeated PIN failures (issue #8, BRD §6.1 FR-07).
 *
 * The schedule is a stepped table rather than a smooth curve so it can match the product spec
 * exactly:
 *
 * - 1–3 failures: no lockout (immediate retry)
 * - 4 failures: 30 seconds
 * - 5 failures: 5 minutes
 * - 6+ failures: 15 minutes
 *
 * [tiers] must be sorted by strictly ascending [BackoffTier.failuresAtLeast]; the lockout for a
 * given failure count is the highest tier whose threshold has been reached.
 */
data class BackoffConfig(
    val tiers: List<BackoffTier> = DEFAULT_TIERS,
) {
    init {
        require(tiers.isNotEmpty()) { "tiers must not be empty" }
        require(tiers.zipWithNext().all { (lower, higher) -> higher.failuresAtLeast > lower.failuresAtLeast }) {
            "tiers must be sorted by strictly ascending failuresAtLeast"
        }
    }

    /** The number of consecutive failures at which the first lockout engages. */
    val firstLockoutAt: Int get() = tiers.first().failuresAtLeast

    companion object {
        val DEFAULT_TIERS: List<BackoffTier> = listOf(
            BackoffTier(failuresAtLeast = 4, lockoutMillis = 30_000L),
            BackoffTier(failuresAtLeast = 5, lockoutMillis = 5 * 60_000L),
            BackoffTier(failuresAtLeast = 6, lockoutMillis = 15 * 60_000L),
        )
    }
}

/** A single step of the [BackoffConfig] schedule: at [failuresAtLeast] failures, wait [lockoutMillis]. */
data class BackoffTier(
    val failuresAtLeast: Int,
    val lockoutMillis: Long,
) {
    init {
        require(failuresAtLeast >= 1) { "failuresAtLeast must be >= 1" }
        require(lockoutMillis >= 0) { "lockoutMillis must be >= 0" }
    }
}

/**
 * Lockout duration for [failedAttempts] consecutive failures: the [BackoffTier.lockoutMillis] of the
 * highest tier whose [BackoffTier.failuresAtLeast] has been reached, or 0 below the first tier.
 */
fun lockoutDurationMillis(failedAttempts: Int, config: BackoffConfig): Long =
    config.tiers.lastOrNull { failedAttempts >= it.failuresAtLeast }?.lockoutMillis ?: 0L
