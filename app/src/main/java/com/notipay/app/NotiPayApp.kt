package com.notipay.app

import android.app.Application
import androidx.room.Room
import com.notipay.app.data.PaymentRepository
import com.notipay.app.data.SettingsRepository
import com.notipay.app.data.local.NotiPayDatabase

/**
 * Application entry point and tiny manual dependency container.
 *
 * Keeping a single shared instance of the database and repositories here lets the
 * notification service and the UI read/write the same state without a DI framework.
 */
class NotiPayApp : Application() {

    private val database by lazy {
        Room.databaseBuilder(this, NotiPayDatabase::class.java, "notipay.db").build()
    }

    val paymentRepository by lazy { PaymentRepository(database.paymentDao()) }

    val settingsRepository by lazy { SettingsRepository(this) }
}
