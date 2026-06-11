package com.mena97villalobos.observability

import co.touchlab.kermit.Logger
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Generates a fresh correlation ID for a user-initiated action. Uses the Kotlin stdlib [Uuid]
 * (available since Kotlin 2.0.20) — no third-party dependency required.
 */
@OptIn(ExperimentalUuidApi::class)
fun newCorrelationId(): String = Uuid.random().toString()

/**
 * Carries the active correlation ID through a coroutine scope so every log statement in the same
 * user action can be tied together. Seed it at an action entry point:
 *
 * ```
 * withContext(CorrelationContext(newCorrelationId())) { /* action work */ }
 * ```
 */
class CorrelationContext(
    val correlationId: String,
) : AbstractCoroutineContextElement(CorrelationContext) {
    companion object Key : CoroutineContext.Key<CorrelationContext>
}

/** The correlation ID active in the current coroutine context, or null if none was set. */
suspend fun currentCorrelationId(): String? =
    coroutineContext[CorrelationContext]?.correlationId

/**
 * Returns a [Logger] whose tag is suffixed with the correlation ID, so it appears on every line
 * logged through it: `MyTag cid=<id>`.
 */
fun Logger.withCorrelation(correlationId: String): Logger =
    withTag("$tag cid=$correlationId")
