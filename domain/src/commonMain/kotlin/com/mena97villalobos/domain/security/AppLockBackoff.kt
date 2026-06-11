package com.mena97villalobos.domain.security

/**
 * Exponential-backoff parameters for repeated PIN failures.
 *
 * No lockout applies until [failureThreshold] consecutive failures. From then on each additional
 * failure doubles the lockout, starting at [baseDelayMillis] and capped at [maxDelayMillis].
 */
data class BackoffConfig(
    val failureThreshold: Int = 3,
    val baseDelayMillis: Long = 30_000L,
    val maxDelayMillis: Long = 15 * 60_000L,
) {
    init {
        require(failureThreshold >= 1) { "failureThreshold must be >= 1" }
        require(baseDelayMillis >= 0) { "baseDelayMillis must be >= 0" }
        require(maxDelayMillis >= baseDelayMillis) { "maxDelayMillis must be >= baseDelayMillis" }
    }
}

/**
 * Lockout duration for [failedAttempts] consecutive failures.
 *
 * Returns 0 below the threshold. At the threshold it returns [BackoffConfig.baseDelayMillis], then
 * doubles per extra failure, saturating at [BackoffConfig.maxDelayMillis]. The doubling is computed
 * without overflow: once it would exceed the cap it returns the cap directly.
 */
fun lockoutDurationMillis(failedAttempts: Int, config: BackoffConfig): Long {
    if (failedAttempts < config.failureThreshold) return 0L
    val exponent = failedAttempts - config.failureThreshold
    var duration = config.baseDelayMillis
    repeat(exponent) {
        if (duration >= config.maxDelayMillis) return config.maxDelayMillis
        duration *= 2
    }
    return duration.coerceAtMost(config.maxDelayMillis)
}
