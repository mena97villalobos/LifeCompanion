package com.mena97villalobos.domain.security

/**
 * Destructive / sensitive operations that the optional passphrase gates when the requirement is
 * enabled (issue #9, BRD §6.1 FR-03, §10.1).
 *
 * Each operation carries a human-readable [label] shown in the confirmation prompt so the user knows
 * exactly what they are authorising.
 *
 * The gating mechanism is [PassphraseManager]: a feature about to perform one of these operations
 * first calls [PassphraseManager.submit] with the user-entered passphrase and only proceeds on
 * [PassphraseOutcome.Success].
 *
 * Note: [DELETE_LOAN], [BULK_DELETE_LOANS], [EXPORT_ALL_DATA] and [RESTORE_BACKUP] correspond to the
 * Loans/Export/Backup features that are not built yet (project Backlog); they are declared here so
 * those features can gate on the passphrase as soon as they land. [DISABLE_PASSPHRASE] and
 * [CHANGE_PASSPHRASE] are wired today (see the settings screen).
 */
enum class SensitiveOperation(val label: String) {
    DELETE_LOAN("Delete loan"),
    BULK_DELETE_LOANS("Delete selected loans"),
    EXPORT_ALL_DATA("Export all data"),
    DISABLE_APP_LOCK("Disable app lock"),
    DISABLE_PASSPHRASE("Disable passphrase requirement"),
    CHANGE_PASSPHRASE("Change passphrase"),
    RESTORE_BACKUP("Restore from backup"),
}
