package com.mena97villalobos.lifecompanion

import androidx.compose.ui.window.ComposeUIViewController
import com.mena97villalobos.lifecompanion.di.hostKoinModules
import com.mena97villalobos.lifecompanion.security.IosArgon2
import com.mena97villalobos.lifecompanion.security.IosArgon2Holder
import com.mena97villalobos.local.di.iosLocalModule
import com.mena97villalobos.observability.Observability
import com.mena97villalobos.remote.di.iosRemoteModule
import org.koin.core.context.startKoin
import org.koin.core.error.KoinApplicationAlreadyStartedException
import platform.Foundation.NSBundle
import kotlin.native.Platform

/**
 * Call once from the iOS app entry (before showing UI), mirroring Android [com.mena97villalobos.lifecompanion.MainApplication].
 *
 * @param argon2 Swift-provided Argon2id bridge (the `Argon2Swift` SPM package), used by the iOS PIN
 *   hasher for app lock (issue #7).
 */
@OptIn(kotlin.experimental.ExperimentalNativeApi::class)
fun initializeLifeCompanionKoinForIos(argon2: IosArgon2) {
    IosArgon2Holder.argon2 = argon2

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
