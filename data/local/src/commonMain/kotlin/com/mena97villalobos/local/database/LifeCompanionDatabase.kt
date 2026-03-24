package com.mena97villalobos.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mena97villalobos.local.dao.WarrantyDao
import com.mena97villalobos.local.database.converters.DateConverter
import com.mena97villalobos.local.entities.WarrantyEntity

@Database(
    entities = [WarrantyEntity::class],
    version = 1,
)
@ConstructedBy(LifeCompanionDatabaseConstructor::class)
@TypeConverters(DateConverter::class)
abstract class LifeCompanionDatabase : RoomDatabase() {
    abstract fun warrantyDao(): WarrantyDao
}

@Suppress("KotlinNoActualForExpect")
expect object LifeCompanionDatabaseConstructor : RoomDatabaseConstructor<LifeCompanionDatabase> {
    override fun initialize(): LifeCompanionDatabase
}

fun getRoomDatabase(builder: RoomDatabase.Builder<LifeCompanionDatabase>): LifeCompanionDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(roomQueryCoroutineContext)
        .build()
