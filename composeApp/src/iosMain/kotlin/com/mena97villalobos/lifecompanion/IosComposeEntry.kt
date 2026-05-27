package com.mena97villalobos.lifecompanion

import androidx.compose.ui.window.ComposeUIViewController
import com.mena97villalobos.lifecompanion.di.hostKoinModules
import com.mena97villalobos.local.di.iosLocalModule
import com.mena97villalobos.observability.Observability
import com.mena97villalobos.remote.di.iosRemoteModule
import org.koin.core.context.startKoin
import org.koin.core.error.KoinApplicationAlreadyStartedException
import platform.Foundation.NSBundle
import kotlin.native.Platform

/**
 * Call once from the iOS app entry (before showing UI), mirroring Android [com.mena97villalobos.lifecompanion.MainApplication].
 */
@OptIn(kotlin.experimental.ExperimentalNativeApi::class)
fun initializeLifeCompanionKoinForIos() {
    // Marketing version from Info.plist (CFBundleShortVersionString); matches the Android versionName.
    val appVersion = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
        ?: "unknown"
    Observability.init(isDebug = Platform.isDebugBinary, appVersion = appVersion)

    runCatching {
        startKoin {
            modules(hostKoinModules(iosLocalModule, iosRemoteModule))
        }
    }.onFailure { throwable ->
        if (throwable !is KoinApplicationAlreadyStartedException) {
            throw throwable
        }
    }
}

fun createLifeCompanionRootViewController() = ComposeUIViewController {
    LifeCompanionApp()
}
