package com.mena97villalobos.local.dao

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mena97villalobos.local.database.LifeCompanionDatabase
import com.mena97villalobos.local.entities.WarrantyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * In-memory [WarrantyDao] tests for the iOS targets. They run on the simulator via
 * `./gradlew :data:local:iosSimulatorArm64Test` and exercise the real Room + bundled SQLite stack
 * with no on-disk database, so each test starts from a clean schema.
 */
class WarrantyDaoTest {

    private lateinit var database: LifeCompanionDatabase
    private lateinit var dao: WarrantyDao

    @BeforeTest
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder<LifeCompanionDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.Default)
            .build()
        dao = database.warrantyDao()
    }

    @AfterTest
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
    fun getAllOrdersByExpiryDateAscending() = runBlocking {
        dao.insert(sampleWarranty(description = "Later", expiryDate = "2030-01-01"))
        dao.insert(sampleWarranty(description = "Sooner", expiryDate = "2027-01-01"))

        val descriptions = dao.getAll().first().map { it.description }

        assertEquals(listOf("Sooner", "Later"), descriptions)
    }

    @Test
    fun updateMutatesStoredWarranty() = runBlocking {
        dao.insert(sampleWarranty(description = "Old name"))
        val stored = dao.getAll().first().single()

        dao.update(stored.copy(description = "New name", notes = "renamed"))

        val updated = dao.getAll().first().single()
        assertEquals(stored.id, updated.id)
        assertEquals("New name", updated.description)
        assertEquals("renamed", updated.notes)
    }

    @Test
    fun deleteRemovesWarrantyById() = runBlocking {
        dao.insert(sampleWarranty(description = "Disposable"))
        val stored = dao.getAll().first().single()

        dao.delete(stored.id)

        assertTrue(dao.getAll().first().isEmpty())
        assertNull(dao.getAll().first().firstOrNull())
    }

    private fun sampleWarranty(
        description: String = "Item",
        storeName: String = "Store",
        expiryDate: String = "2027-01-01",
    ) = WarrantyEntity(
        description = description,
        storeName = storeName,
        purchaseDate = LocalDate.parse("2026-01-01"),
        expiryDate = LocalDate.parse(expiryDate),
        notes = null,
        imageObjectId = null,
    )
}
