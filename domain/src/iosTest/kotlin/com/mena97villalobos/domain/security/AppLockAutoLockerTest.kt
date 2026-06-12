package com.mena97villalobos.domain.security

import com.mena97villalobos.domain.model.AppLockState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Behavioural tests for [AppLockAutoLocker] on the iOS simulator
 * (`./gradlew :domain:iosSimulatorArm64Test`). A controllable clock makes the elapsed-time check
 * deterministic, and a hot [MutableSharedFlow] feeds lifecycle events.
 */
class AppLockAutoLockerTest {

    private class FakeLifecycleObserver : AppLifecycleObserver {
        val emitter = MutableSharedFlow<AppLifecycleEvent>(extraBufferCapacity = 8)
        override val events: Flow<AppLifecycleEvent> = emitter
    }

    private fun unlockedManager(repo: FakeAppLockRepository, now: () -> Long): AppLockManager {
        val manager = AppLockManager(repo, now = now)
        manager.markUnlocked()
        return manager
    }

    @Test
    fun backgroundThenForegroundPastTimeoutLocks() = runBlocking {
        val repo = FakeAppLockRepository().apply {
            storedPin = "123456"
            setInactivityTimeoutMillis(5 * 60_000L)
        }
        var now = 0L
        val manager = unlockedManager(repo) { now }
        val observer = FakeLifecycleObserver()
        val autoLocker = AppLockAutoLocker(manager, observer, repo) { now }

        val job = launch { autoLocker.observe() }
        yield()

        observer.emitter.emit(AppLifecycleEvent.BACKGROUND)
        yield()
        // Return after 6 minutes — past the 5 minute timeout.
        now = 6 * 60_000L
        observer.emitter.emit(AppLifecycleEvent.FOREGROUND)
        yield()

        assertIs<AppLockState.Locked>(manager.state.value)
        job.cancel()
    }

    @Test
    fun briefBackgroundDoesNotLock() = runBlocking {
        val repo = FakeAppLockRepository().apply {
            storedPin = "123456"
            setInactivityTimeoutMillis(5 * 60_000L)
        }
        var now = 0L
        val manager = unlockedManager(repo) { now }
        val observer = FakeLifecycleObserver()
        val autoLocker = AppLockAutoLocker(manager, observer, repo) { now }

        val job = launch { autoLocker.observe() }
        yield()

        observer.emitter.emit(AppLifecycleEvent.BACKGROUND)
        yield()
        // Return after only 30 seconds — well under the timeout.
        now = 30_000L
        observer.emitter.emit(AppLifecycleEvent.FOREGROUND)
        yield()

        assertEquals(AppLockState.Unlocked, manager.state.value)
        job.cancel()
    }

    @Test
    fun ignoresLifecycleWhenAlreadyLocked() = runBlocking {
        val repo = FakeAppLockRepository().apply { storedPin = "123456" }
        var now = 0L
        val manager = AppLockManager(repo, now = { now })
        manager.initialize() // Locked, not Unlocked.
        val observer = FakeLifecycleObserver()
        val autoLocker = AppLockAutoLocker(manager, observer, repo) { now }

        val job = launch { autoLocker.observe() }
        yield()

        observer.emitter.emit(AppLifecycleEvent.BACKGROUND)
        yield()
        now = 10 * 60_000L
        observer.emitter.emit(AppLifecycleEvent.FOREGROUND)
        yield()

        // Still just Locked — no crash, no state churn from the auto-locker.
        assertIs<AppLockState.Locked>(manager.state.value)
        job.cancel()
    }
}
