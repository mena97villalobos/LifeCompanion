package com.mena97villalobos.domain.usecases

import com.mena97villalobos.domain.security.FakePassphraseRepository
import com.mena97villalobos.domain.security.PassphraseManager
import com.mena97villalobos.domain.security.PassphraseOutcome
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PassphraseUseCasesTest {

    private val validPassphrase = "correct horse battery"
    private val anotherValid = "another valid passphrase"

    @Test
    fun validationRequiresMinimumLength() {
        assertFalse(Passphrase.isValid("short"))
        assertFalse(Passphrase.isValid("a".repeat(Passphrase.MIN_LENGTH - 1)))
        assertTrue(Passphrase.isValid("a".repeat(Passphrase.MIN_LENGTH)))
    }

    @Test
    fun enableRejectsTooShortPassphrase() = runBlocking {
        val repo = FakePassphraseRepository()
        val enable = EnablePassphraseUseCase(repo)

        assertFailsWith<IllegalArgumentException> { enable("short") }
        assertNull(repo.storedPassphrase)
    }

    @Test
    fun enableStoresValidPassphrase() = runBlocking {
        val repo = FakePassphraseRepository()
        EnablePassphraseUseCase(repo)(validPassphrase)

        assertEquals(validPassphrase, repo.storedPassphrase)
        assertTrue(repo.isEnabled())
    }

    @Test
    fun disableRequiresCorrectPassphrase() = runBlocking {
        val repo = FakePassphraseRepository().apply { storedPassphrase = validPassphrase }
        val disable = DisablePassphraseUseCase(PassphraseManager(repo) { 1_000L }, repo)

        val wrong = disable("nope this is wrong")
        assertIs<PassphraseOutcome.Failed>(wrong)
        assertTrue(repo.isEnabled())

        val right = disable(validPassphrase)
        assertEquals(PassphraseOutcome.Success, right)
        assertFalse(repo.isEnabled())
    }

    @Test
    fun changeRequiresCurrentAndValidNew() = runBlocking {
        val repo = FakePassphraseRepository().apply { storedPassphrase = validPassphrase }
        val change = ChangePassphraseUseCase(PassphraseManager(repo) { 1_000L }, repo)

        assertFailsWith<IllegalArgumentException> { change(validPassphrase, "short") }
        assertEquals(validPassphrase, repo.storedPassphrase)

        val wrongCurrent = change("wrong current pass", anotherValid)
        assertIs<PassphraseOutcome.Failed>(wrongCurrent)
        assertEquals(validPassphrase, repo.storedPassphrase)

        val ok = change(validPassphrase, anotherValid)
        assertEquals(PassphraseOutcome.Success, ok)
        assertEquals(anotherValid, repo.storedPassphrase)
    }
}
