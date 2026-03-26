package com.mena97villalobos.domain.services

import kotlinx.coroutines.CoroutineDispatcher

internal expect object DomainDispatchers {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
}

/**
 * Default [DispatcherService] for Android and iOS hosts. Use with Koin:
 * `single<DispatcherService> { createDefaultDispatcherService() }`.
 */
fun createDefaultDispatcherService(): DispatcherService = object : DispatcherService {
    override val io: CoroutineDispatcher get() = DomainDispatchers.io
    override val default: CoroutineDispatcher get() = DomainDispatchers.default
    override val main: CoroutineDispatcher get() = DomainDispatchers.main
}
