package com.notipay.app.domain

/**
 * A parsed payment confirmation, independent of the source wallet (Yape, Plin, ...).
 *
 * @property senderName name of the sender (the wallet may mask the surname)
 * @property amountCents amount in integer cents — never use floating point for money
 * @property securityCode security code, if present in the notification
 */
data class Payment(
    val senderName: String,
    val amountCents: Long,
    val securityCode: String?,
)
