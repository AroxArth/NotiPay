package com.notipay.app.notification

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.notipay.app.BuildConfig
import com.notipay.app.NotiPayApp
import com.notipay.app.domain.YapeParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Listens for notifications posted by the official Yape app, records each payment,
 * and announces it out loud — unless reading is paused.
 *
 * The user must grant "notification access" in system settings before the OS binds
 * this service.
 */
class YapeNotificationListener : NotificationListenerService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var announcer: PaymentAnnouncer? = null

    private val app: NotiPayApp
        get() = application as NotiPayApp

    override fun onCreate() {
        super.onCreate()
        announcer = PaymentAnnouncer(this)
    }

    override fun onDestroy() {
        announcer?.shutdown()
        announcer = null
        scope.cancel()
        super.onDestroy()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()

        // DEBUG ONLY: logs every notification so you can confirm Yape's real package
        // name on the test device (filter Logcat by the tag "NotiPay"). If payments
        // are not announced, check this log and update YAPE_PACKAGE below.
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "pkg=$packageName | title=$title | text=$text")
        }

        if (packageName != YAPE_PACKAGE) return
        if (app.settingsRepository.isPausedNow()) return

        val payment = YapeParser.parse(text) ?: return
        val receivedAt = System.currentTimeMillis()

        scope.launch { app.paymentRepository.record(payment, receivedAt) }
        announcer?.announce(payment)
    }

    companion object {
        private const val TAG = "NotiPay"

        // Official Yape app package name. If announcements never trigger on the real
        // device, confirm the actual package via the DEBUG log above and update this.
        const val YAPE_PACKAGE = "com.bcp.innovacxion.yapeapp"
    }
}
