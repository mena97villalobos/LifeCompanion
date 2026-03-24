package com.mena97villalobos.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getLifeCompanionDatabaseBuilder(context: Context): RoomDatabase.Builder<LifeCompanionDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("app_database")
    return Room.databaseBuilder<LifeCompanionDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}
