package com.mena97villalobos.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mena97villalobos.local.dao.WarrantyDao
import com.mena97villalobos.local.database.converters.DateConverter
import com.mena97villalobos.local.entities.WarrantyEntity

@Database(
    entities = [WarrantyEntity::class],
    version = 1,
)
@TypeConverters(DateConverter::class)
abstract class LifeCompanionDatabase : RoomDatabase() {
    abstract fun warrantyDao(): WarrantyDao
}
