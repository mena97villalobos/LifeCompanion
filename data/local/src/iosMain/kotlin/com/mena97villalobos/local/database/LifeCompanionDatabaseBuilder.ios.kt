@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mena97villalobos.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getLifeCompanionDatabaseBuilder(): RoomDatabase.Builder<LifeCompanionDatabase> {
    val dbFilePath = documentDirectory() + "/app_database"
    return Room.databaseBuilder<LifeCompanionDatabase>(
        name = dbFilePath,
    )
}

private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}
