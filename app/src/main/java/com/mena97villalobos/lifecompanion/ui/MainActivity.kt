package com.mena97villalobos.lifecompanion.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.mena97villalobos.lifecompanion.LifeCompanionApp
import com.mena97villalobos.lifecompanion.security.AndroidActivityProvider

/**
 * Single-activity host. Extends [FragmentActivity] because `BiometricPrompt` requires one, and
 * registers itself with [AndroidActivityProvider] so the biometric authenticator can attach to it.
 */
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AndroidActivityProvider.set(this)
        setContent {
            LifeCompanionApp()
        }
    }

    override fun onDestroy() {
        AndroidActivityProvider.clear(this)
        super.onDestroy()
    }
}
