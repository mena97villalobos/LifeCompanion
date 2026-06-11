package com.mena97villalobos.local.dao

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mena97villalobos.domain.model.UserProfile
import com.mena97villalobos.local.database.LifeCompanionDatabase
import com.mena97villalobos.local.entities.UserProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * In-memory [UserProfileDao] tests for the iOS targets
 * (`./gradlew :data:local:iosSimulatorArm64Test`). Verifies single-row upsert semantics that back
 * the offline-first profile (issue #7).
 */
class UserProfileDaoTest {

    private lateinit var database: LifeCompanionDatabase
    private lateinit var dao: UserProfileDao

    @BeforeTest
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder<LifeCompanionDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.Default)
            .build()
        dao = database.userProfileDao()
    }

    @AfterTest
    fun tearDown() {
        if (::database.isInitialized) {
            database.close()
        }
    }

    @Test
    fun getReturnsNullBeforeOnboarding() = runBlocking {
        assertNull(dao.get())
        assertNull(dao.observe().first())
    }

    @Test
    fun upsertThenReadReturnsProfile() = runBlocking {
        dao.upsert(sampleProfile(displayName = "Bryan"))

        val stored = dao.get()

        assertEquals("Bryan", stored?.displayName)
        assertEquals("CRC", stored?.currencyCode)
        assertEquals("es-CR", stored?.localeTag)
    }

    @Test
    fun upsertReplacesSingletonRow() = runBlocking {
        dao.upsert(sampleProfile(displayName = "First"))
        dao.upsert(sampleProfile(displayName = "Second", currencyCode = "USD"))

        assertEquals("Second", dao.get()?.displayName)
        assertEquals("USD", dao.get()?.currencyCode)
        // Replacing the singleton must not create a second row.
        assertEquals(UserProfile.SINGLETON_ID, dao.get()?.id)
    }

    @Test
    fun clearRemovesProfile() = runBlocking {
        dao.upsert(sampleProfile())

        dao.clear()

        assertNull(dao.get())
    }

    private fun sampleProfile(
        displayName: String = "User",
        currencyCode: String = "CRC",
        localeTag: String = "es-CR",
    ) = UserProfileEntity(
        id = UserProfile.SINGLETON_ID,
        displayName = displayName,
        currencyCode = currencyCode,
        localeTag = localeTag,
    )
}
