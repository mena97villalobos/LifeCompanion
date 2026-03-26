package com.mena97villalobos.local.database

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

internal actual val roomQueryCoroutineContext: CoroutineContext
    get() = Dispatchers.IO
