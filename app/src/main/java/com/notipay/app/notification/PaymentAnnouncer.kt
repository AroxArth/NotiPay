package com.notipay.app.notification

import android.content.Context
import android.speech.tts.TextToSpeech
import com.notipay.app.domain.Payment
import com.notipay.app.domain.spokenAmount
import java.util.Locale

/**
 * Speaks a Yape payment confirmation out loud using the system Text-to-Speech engine.
 *
 * Audio plays through whatever output device is active, so a paired Bluetooth speaker
 * is handled automatically by the OS — no Bluetooth code is required here.
 */
class PaymentAnnouncer(context: Context) : TextToSpeech.OnInitListener {

    private val tts = TextToSpeech(context.applicationContext, this)
    private var ready = false
    private var pendingMessage: String? = null

    /** When true, the spoken message also includes the security code. Off by default. */
    var speakSecurityCode: Boolean = false

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return

        val result = tts.setLanguage(Locale("es", "PE"))
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts.language = Locale("es")
        }

        applyBestVoice()
        tts.setSpeechRate(0.95f)

        ready = true
        pendingMessage?.let {
            enqueue(it)
            pendingMessage = null
        }
    }

    /** Picks the highest-quality offline Spanish voice available on the device, if any. */
    private fun applyBestVoice() {
        runCatching {
            val best = tts.voices
                ?.filter { it.locale.language == "es" && !it.isNetworkConnectionRequired }
                ?.maxByOrNull { it.quality }
            if (best != null) tts.voice = best
        }
    }

    fun announce(payment: Payment) {
        val message = buildString {
            append("Confirmación de pago. ")
            append(payment.senderName)
            append(" te envió ")
            append(spokenAmount(payment.amountCents))
            append(".")
            if (speakSecurityCode && payment.securityCode != null) {
                append(" Código de seguridad: ")
                append(payment.securityCode)
                append(".")
            }
        }

        enqueue(message)
    }

    /** Speaks a neutral phrase to verify audio output without faking a real payment. */
    fun announceTest() {
        enqueue("Prueba de audio de NotiPay. Todo funciona correctamente.")
    }

    private fun enqueue(message: String) {
        if (ready) speakNow(message) else pendingMessage = message
    }

    private fun speakNow(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_ADD, null, "notipay")
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}
