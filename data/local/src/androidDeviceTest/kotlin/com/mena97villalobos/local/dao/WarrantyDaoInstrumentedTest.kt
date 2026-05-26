package com.mena97villalobos.local.dao

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mena97villalobos.local.database.LifeCompanionDatabase
import com.mena97villalobos.local.entities.WarrantyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * In-memory [WarrantyDao] tests for Android. Android's [Room.inMemoryDatabaseBuilder] requires a
 * [android.content.Context], so these run as instrumented tests on a device/emulator via
 * `./gradlew :data:local:connectedAndroidTest`.
 */
@RunWith(AndroidJUnit4::class)
class WarrantyDaoInstrumentedTest {

    private lateinit var database: LifeCompanionDatabase
    private lateinit var dao: WarrantyDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder<LifeCompanionDatabase>(context)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        dao = database.warrantyDao()
    }

    @After
    fun tearDown() {
        if (::database.isInitialized) {
            database.close()
        }
    }

    @Test
    fun insertThenQueryReturnsStoredWarranty() = runBlocking {
        dao.insert(sampleWarranty(description = "Laptop"))

        val stored = dao.getAll().first()

        assertEquals(1, stored.size)
        assertEquals("Laptop", stored.first().description)
    }

    @Test
    fun updateMutatesStoredWarranty() = runBlocking {
        dao.insert(sampleWarranty(description = "Old name"))
        val stored = dao.getAll().first().single()

        dao.update(stored.copy(description = "New name"))

        val updated = dao.getAll().first().single()
        assertEquals(stored.id, updated.id)
        assertEquals("New name", updated.description)
    }

    @Test
    fun deleteRemovesWarrantyById() = runBlocking {
        dao.insert(sampleWarranty(description = "Disposable"))
        val stored = dao.getAll().first().single()

        dao.delete(stored.id)

        assertTrue(dao.getAll().first().isEmpty())
    }

    private fun sampleWarranty(
        description: String = "Item",
        storeName: String = "Store",
    ) = WarrantyEntity(
        description = description,
        storeName = storeName,
        purchaseDate = LocalDate.parse("2026-01-01"),
        expiryDate = LocalDate.parse("2027-01-01"),
        notes = null,
        imageObjectId = null,
    )
}
