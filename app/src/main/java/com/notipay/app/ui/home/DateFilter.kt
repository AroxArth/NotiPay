package com.notipay.app.ui.home

import java.time.ZoneId
import java.time.ZonedDateTime

/** Time window used to filter the payment history. */
enum class DateFilter(val label: String) {
    TODAY("Hoy"),
    MONTH("Mes"),
    YEAR("Año"),
    ALL("Todo");

    /** Epoch-millis lower bound for this filter. Pair it with [Long.MAX_VALUE] as the upper bound. */
    fun startMillis(zone: ZoneId = ZoneId.systemDefault()): Long {
        val today = ZonedDateTime.now(zone).toLocalDate()
        val start = when (this) {
            TODAY -> today.atStartOfDay(zone)
            MONTH -> today.withDayOfMonth(1).atStartOfDay(zone)
            YEAR -> today.withDayOfYear(1).atStartOfDay(zone)
            ALL -> return 0L
        }
        return start.toInstant().toEpochMilli()
    }
}
