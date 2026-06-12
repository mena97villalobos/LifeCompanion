package com.mena97villalobos.lifecompanion.security

import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference

/**
 * Holds a weak reference to the currently resumed [FragmentActivity] so the
 * `BiometricPrompt`-backed authenticator can attach to it. The host activity registers/unregisters
 * itself in its lifecycle callbacks; the weak reference avoids leaking a destroyed activity.
 */
object AndroidActivityProvider {
    private var activityRef: WeakReference<FragmentActivity>? = null

    val current: FragmentActivity?
        get() = activityRef?.get()

    fun set(activity: FragmentActivity) {
        activityRef = WeakReference(activity)
    }

    fun clear(activity: FragmentActivity) {
        if (activityRef?.get() === activity) {
            activityRef = null
        }
    }
}
