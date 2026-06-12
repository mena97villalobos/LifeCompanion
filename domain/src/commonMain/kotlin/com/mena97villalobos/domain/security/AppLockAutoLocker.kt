package com.mena97villalobos.domain.security

import com.mena97villalobos.domain.model.AppLockState
import com.mena97villalobos.domain.repository.AppLockRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.time.Clock

/**
 * Drives the inactivity auto-lock: re-engages [AppLockManager.lock] once the app has spent longer
 * than the user-configured inactivity timeout in the background.
 *
 * Two complementary mechanisms make this robust to mobile process suspension:
 *
 * 1. On backgrounding (while unlocked) a timer is armed for the timeout; if the process stays alive
 *    that long it locks proactively.
 * 2. On foregrounding the elapsed background time is checked against the timeout — this catches the
 *    common case where the OS suspended the process and the timer never fired.
 *
 * Brief backgrounding (shorter than the timeout) leaves the app unlocked, so quick app switches do
 * not re-prompt. The timeout is read fresh from [AppLockRepository] each time so settings changes
 * take effect immediately.
 */
class AppLockAutoLocker(
    private val appLockManager: AppLockManager,
    private val lifecycleObserver: AppLifecycleObserver,
    private val repository: AppLockRepository,
    private val now: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) {

    /** Collects lifecycle events forever; launch this in an app-scoped coroutine. */
    suspend fun observe() = coroutineScope {
        var backgroundedAt: Long? = null
        var timerJob: Job? = null

        lifecycleObserver.events.collect { event ->
            when (event) {
                AppLifecycleEvent.BACKGROUND -> {
                    // Only meaningful while currently unlocked; otherwise the lock already holds.
                    if (appLockManager.state.value is AppLockState.Unlocked) {
                        backgroundedAt = now()
                        val timeout = repository.getInactivityTimeoutMillis()
                        timerJob?.cancel()
                        timerJob = launch {
                            delay(timeout)
                            appLockManager.lock()
                        }
                    }
                }

                AppLifecycleEvent.FOREGROUND -> {
                    timerJob?.cancel()
                    timerJob = null
                    val since = backgroundedAt
                    backgroundedAt = null
                    if (since != null && appLockManager.state.value is AppLockState.Unlocked) {
                        val timeout = repository.getInactivityTimeoutMillis()
                        if (now() - since >= timeout) {
                            appLockManager.lock()
                        }
                    }
                }
            }
        }
    }
}
