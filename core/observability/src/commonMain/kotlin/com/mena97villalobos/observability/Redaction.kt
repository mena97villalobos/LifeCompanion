package com.mena97villalobos.observability

private const val REDACTED_EMAIL = "[REDACTED_EMAIL]"
private const val REDACTED_AMOUNT = "[REDACTED_AMOUNT]"

private val emailRegex = Regex("""[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}""")

// Monetary amounts: a currency marker (₡, $, CRC, USD) adjacent to a number, in either order.
// Covers Costa Rican formatting (₡1.234.567,89) and USD ($1,234.56 / 1234.56 USD).
private val moneyRegex = Regex(
    """(?:[₡$]|\b(?:CRC|USD)\b)\s?\d[\d.,]*|\d[\d.,]*\s?(?:[₡$]|\b(?:CRC|USD)\b)""",
)

/**
 * Removes the categories of PII the BRD forbids in logs (emails and monetary amounts such as
 * payment amounts / loan principals) from a log message. Pure and deterministic so it can be
 * unit-tested directly. Names are not auto-detectable; the convention (see `docs/logging.md`) is to
 * never log them — this scrubber is a safety net, not a substitute for that discipline.
 */
fun redact(message: String): String =
    message
        .replace(emailRegex, REDACTED_EMAIL)
        .replace(moneyRegex, REDACTED_AMOUNT)
