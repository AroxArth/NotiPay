package com.notipay.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class YapeParserTest {

    @Test
    fun parsesRealNotification() {
        val payment = YapeParser.parse(
            "Una Persona* te envió un pago por S/ 0.1. El cód. de seguridad es: 514",
        )
        assertEquals("Una Persona", payment?.senderName)
        assertEquals(10L, payment?.amountCents)
        assertEquals("514", payment?.securityCode)
    }

    @Test
    fun parsesIntegerAmount() {
        val payment = YapeParser.parse(
            "Maria Lopez te envió un pago por S/ 50. El cód. de seguridad es: 999",
        )
        assertEquals("Maria Lopez", payment?.senderName)
        assertEquals(5000L, payment?.amountCents)
        assertEquals("999", payment?.securityCode)
    }

    @Test
    fun normalizesCommaDecimalToCents() {
        val payment = YapeParser.parse("Ana Perez te envió un pago por S/ 1,50.")
        assertEquals("Ana Perez", payment?.senderName)
        assertEquals(150L, payment?.amountCents)
        assertNull(payment?.securityCode)
    }

    @Test
    fun returnsNullForUnrelatedText() {
        assertNull(YapeParser.parse("Tu recarga fue exitosa"))
        assertNull(YapeParser.parse(""))
        assertNull(YapeParser.parse(null))
    }
}
