package com.mena97villalobos.lifecompanion.security

import com.mena97villalobos.domain.security.AppLifecycleEvent
import com.mena97villalobos.domain.security.AppLifecycleObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationWillEnterForegroundNotification

/**
 * [AppLifecycleObserver] backed by `NSNotificationCenter`, observing the UIKit app lifecycle
 * notifications directly from Kotlin/Native — no Swift bridging required. Observers are delivered on
 * the main queue and removed when the flow is cancelled.
 */
class IosAppLifecycleObserver : AppLifecycleObserver {
    override val events: Flow<AppLifecycleEvent> = callbackFlow {
        val center = NSNotificationCenter.defaultCenter
        val mainQueue = NSOperationQueue.mainQueue

        val foregroundToken = center.addObserverForName(
            name = UIApplicationWillEnterForegroundNotification,
            `object` = null,
            queue = mainQueue,
        ) { trySend(AppLifecycleEvent.FOREGROUND) }

        val backgroundToken = center.addObserverForName(
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null,
            queue = mainQueue,
        ) { trySend(AppLifecycleEvent.BACKGROUND) }

        awaitClose {
            center.removeObserver(foregroundToken)
            center.removeObserver(backgroundToken)
        }
    }
}
