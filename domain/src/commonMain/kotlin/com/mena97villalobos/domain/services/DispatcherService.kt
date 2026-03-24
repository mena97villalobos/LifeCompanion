package com.mena97villalobos.domain.services

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherService {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
}
