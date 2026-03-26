package com.mena97villalobos.domain.services

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Host-provided coroutine dispatchers so data layers can stay testable and platform-agnostic.
 * Wire with [com.mena97villalobos.domain.services.createDefaultDispatcherService] in Koin.
 */
interface DispatcherService {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
}
