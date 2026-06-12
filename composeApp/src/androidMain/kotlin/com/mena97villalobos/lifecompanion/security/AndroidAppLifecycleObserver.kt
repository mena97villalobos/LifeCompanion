package com.mena97villalobos.lifecompanion.security

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.mena97villalobos.domain.security.AppLifecycleEvent
import com.mena97villalobos.domain.security.AppLifecycleObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

/**
 * [AppLifecycleObserver] backed by [ProcessLifecycleOwner], which tracks whether *any* of the app's
 * activities are started — i.e. whether the whole app is in the foreground. `onStart`/`onStop` fire
 * on the main thread, so the observer is added/removed there.
 */
class AndroidAppLifecycleObserver : AppLifecycleObserver {
    override val events: Flow<AppLifecycleEvent> = callbackFlow {
        val observer = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                trySend(AppLifecycleEvent.FOREGROUND)
            }

            override fun onStop(owner: LifecycleOwner) {
                trySend(AppLifecycleEvent.BACKGROUND)
            }
        }
        val lifecycle = ProcessLifecycleOwner.get().lifecycle
        lifecycle.addObserver(observer)
        awaitClose { lifecycle.removeObserver(observer) }
    }.flowOn(Dispatchers.Main.immediate)
}
