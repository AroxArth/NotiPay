package com.notipay.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PaymentEntity::class], version = 1, exportSchema = false)
abstract class NotiPayDatabase : RoomDatabase() {
    abstract fun paymentDao(): PaymentDao
}
