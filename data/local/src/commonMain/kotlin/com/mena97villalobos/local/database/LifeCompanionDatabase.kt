package com.mena97villalobos.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import com.mena97villalobos.local.dao.UserProfileDao
import com.mena97villalobos.local.dao.WarrantyDao
import com.mena97villalobos.local.database.converters.DateConverter
import com.mena97villalobos.local.entities.UserProfileEntity
import com.mena97villalobos.local.entities.WarrantyEntity

@Database(
    entities = [WarrantyEntity::class, UserProfileEntity::class],
    version = 2,
)
@ConstructedBy(LifeCompanionDatabaseConstructor::class)
@TypeConverters(DateConverter::class)
abstract class LifeCompanionDatabase : RoomDatabase() {
    abstract fun warrantyDao(): WarrantyDao

    abstract fun userProfileDao(): UserProfileDao
}

@Suppress("KotlinNoActualForExpect")
expect object LifeCompanionDatabaseConstructor : RoomDatabaseConstructor<LifeCompanionDatabase> {
    override fun initialize(): LifeCompanionDatabase
}

/**
 * v1 -> v2: adds the offline-first user profile table (issue #7). Warranty data is untouched, so the
 * migration only creates the new `user_profile` table.
 */
val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `user_profile` (" +
                "`id` INTEGER NOT NULL, " +
                "`displayName` TEXT NOT NULL, " +
                "`currencyCode` TEXT NOT NULL, " +
                "`localeTag` TEXT NOT NULL, " +
                "PRIMARY KEY(`id`))",
        )
    }
}

fun getRoomDatabase(builder: RoomDatabase.Builder<LifeCompanionDatabase>): LifeCompanionDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .addMigrations(MIGRATION_1_2)
        .setQueryCoroutineContext(roomQueryCoroutineContext)
        .build()
