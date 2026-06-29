package com.notipay.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderName: String,
    val amountCents: Long,
    val securityCode: String?,
    val receivedAt: Long,
)
