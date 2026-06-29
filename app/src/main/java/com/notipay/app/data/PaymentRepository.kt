package com.notipay.app.data

import com.notipay.app.data.local.PaymentDao
import com.notipay.app.data.local.PaymentEntity
import com.notipay.app.domain.Payment
import kotlinx.coroutines.flow.Flow

class PaymentRepository(private val dao: PaymentDao) {

    suspend fun record(payment: Payment, receivedAt: Long) {
        dao.insert(
            PaymentEntity(
                senderName = payment.senderName,
                amountCents = payment.amountCents,
                securityCode = payment.securityCode,
                receivedAt = receivedAt,
            ),
        )
    }

    fun observePayments(start: Long, end: Long): Flow<List<PaymentEntity>> =
        dao.observeBetween(start, end)

    fun observeTotal(start: Long, end: Long): Flow<Long> =
        dao.observeTotalBetween(start, end)
}
