package com.notipay.app.domain

/**
 * Parses the text of a Yape "Confirmación de Pago" notification.
 *
 * Pure Kotlin, no Android dependencies, so it can be covered by fast unit tests.
 *
 * Expected body format:
 *   "Una Persona* te envió un pago por S/ 0.1. El cód. de seguridad es: 514"
 */
object YapeParser {

    private val PAYMENT_REGEX = Regex(
        """^(.+?)\s+te envió un pago por\s+S/\s*([\d]+(?:[.,]\d{1,2})?)""",
        RegexOption.IGNORE_CASE,
    )

    private val CODE_REGEX = Regex(
        """seguridad es:\s*(\d+)""",
        RegexOption.IGNORE_CASE,
    )

    fun parse(text: String?): Payment? {
        if (text.isNullOrBlank()) return null

        val match = PAYMENT_REGEX.find(text.trim()) ?: return null

        // Yape masks the surname with a trailing '*' (e.g. "Una Persona*").
        val sender = match.groupValues[1].trim().trimEnd('*').trim()
        val amountCents = toCents(match.groupValues[2]) ?: return null
        val code = CODE_REGEX.find(text)?.groupValues?.get(1)

        if (sender.isBlank()) return null

        return Payment(senderName = sender, amountCents = amountCents, securityCode = code)
    }

    /** Converts a raw amount such as "0.1", "1,50" or "50" into integer cents. */
    private fun toCents(raw: String): Long? {
        val parts = raw.replace(',', '.').split('.')
        val soles = parts[0].toLongOrNull() ?: return null
        val cents = if (parts.size > 1) {
            parts[1].padEnd(2, '0').take(2).toLongOrNull() ?: return null
        } else {
            0L
        }
        return soles * 100 + cents
    }
}
