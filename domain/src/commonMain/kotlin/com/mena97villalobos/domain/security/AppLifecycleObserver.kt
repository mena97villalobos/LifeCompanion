package com.mena97villalobos.domain.security

import kotlinx.coroutines.flow.Flow

/** Foreground/background transitions of the whole app process. */
enum class AppLifecycleEvent {
    /** The app moved to the foreground (became visible / interactive). */
    FOREGROUND,

    /** The app moved to the background (no longer visible). */
    BACKGROUND,
}

/**
 * Observes process-level foreground/background transitions, abstracting the platform mechanism:
 *
 * - **Android**: `ProcessLifecycleOwner` (`androidx.lifecycle:lifecycle-process`).
 * - **iOS**: `UIApplicationWillEnterForegroundNotification` /
 *   `UIApplicationDidEnterBackgroundNotification` via `NSNotificationCenter`.
 *
 * Kept in `domain` as a pure interface so [AppLockAutoLocker] stays platform-agnostic and unit
 * testable; the actual implementations live in the host module's platform source sets.
 */
interface AppLifecycleObserver {
    val events: Flow<AppLifecycleEvent>
}
