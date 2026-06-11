package com.mena97villalobos.observability

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CorrelationTest {

    @Test
    fun generatesUuidShapedIds() {
        val id = newCorrelationId()
        // Canonical UUID: 8-4-4-4-12 hex groups.
        val uuidRegex = Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")
        assertTrue(uuidRegex.matches(id), "unexpected correlation id format: $id")
    }

    @Test
    fun generatesUniqueIds() {
        val ids = List(1000) { newCorrelationId() }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun distinctCallsDiffer() {
        assertNotEquals(newCorrelationId(), newCorrelationId())
    }
}
