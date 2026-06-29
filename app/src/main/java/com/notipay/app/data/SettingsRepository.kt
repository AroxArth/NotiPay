package com.notipay.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Stores the "paused" flag in SharedPreferences.
 *
 * The UI observes [isPaused] reactively; the notification service reads
 * [isPausedNow] synchronously on each incoming notification.
 */
class SettingsRepository(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("notipay_settings", Context.MODE_PRIVATE)

    private val _isPaused = MutableStateFlow(prefs.getBoolean(KEY_PAUSED, false))
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    fun isPausedNow(): Boolean = prefs.getBoolean(KEY_PAUSED, false)

    fun setPaused(paused: Boolean) {
        prefs.edit().putBoolean(KEY_PAUSED, paused).apply()
        _isPaused.value = paused
    }

    private companion object {
        const val KEY_PAUSED = "is_paused"
    }
}
