package com.notipay.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {

    @Insert
    suspend fun insert(payment: PaymentEntity)

    @Query("SELECT * FROM payments WHERE receivedAt BETWEEN :start AND :end ORDER BY receivedAt DESC")
    fun observeBetween(start: Long, end: Long): Flow<List<PaymentEntity>>

    @Query("SELECT COALESCE(SUM(amountCents), 0) FROM payments WHERE receivedAt BETWEEN :start AND :end")
    fun observeTotalBetween(start: Long, end: Long): Flow<Long>
}
