package com.notipay.app.domain

import kotlin.math.absoluteValue

/** Formats an amount stored in integer cents as "S/ 0.10". */
fun formatSoles(cents: Long): String {
    val soles = cents / 100
    val remainder = (cents % 100).absoluteValue
    return "S/ %d.%02d".format(soles, remainder)
}

/**
 * Builds a Spanish spoken form of an amount in cents.
 *   10  -> "10 céntimos"
 *   150 -> "un sol con 50 céntimos"
 *   5000 -> "50 soles"
 */
fun spokenAmount(cents: Long): String {
    val soles = cents / 100
    val remainder = (cents % 100).absoluteValue.toInt()

    val solesPart = when (soles) {
        0L -> null
        1L -> "un sol"
        else -> "$soles soles"
    }
    val centsPart = when (remainder) {
        0 -> null
        1 -> "un céntimo"
        else -> "$remainder céntimos"
    }

    return listOfNotNull(solesPart, centsPart)
        .joinToString(" con ")
        .ifEmpty { "cero soles" }
}
