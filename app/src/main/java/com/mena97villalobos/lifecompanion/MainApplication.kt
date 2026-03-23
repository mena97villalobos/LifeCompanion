package com.mena97villalobos.lifecompanion

import android.app.Application
import com.mena97villalobos.lifecompanion.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.logger.slf4jLogger

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            val logLevel = if (BuildConfig.DEBUG) Level.DEBUG else Level.NONE
            androidLogger(logLevel)
            slf4jLogger(logLevel)
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}
