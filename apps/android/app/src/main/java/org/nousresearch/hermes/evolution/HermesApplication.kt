package org.nousresearch.hermes.evolution

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log

class HermesApplication : Application() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "hermes_agent_service_channel"
        private const val PREFS_NAME = "hermes_agent_prefs"
        const val PREF_GATEWAY_URL = "gateway_url"
        const val DEFAULT_GATEWAY_URL = "http://127.0.0.1:8080/"
        const val PREF_WAKELOCK_ENABLED = "wakelock_enabled"

        private const val TAG = "HermesApplication"
        lateinit var instance: HermesApplication
            private set
    }

    val prefs: SharedPreferences by lazy {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var gatewayUrl: String
        get() = prefs.getString(PREF_GATEWAY_URL, DEFAULT_GATEWAY_URL) ?: DEFAULT_GATEWAY_URL
        set(value) = prefs.edit().putString(PREF_GATEWAY_URL, value).apply()

    var isWakeLockEnabled: Boolean
        get() = prefs.getBoolean(PREF_WAKELOCK_ENABLED, false)
        set(value) = prefs.edit().putBoolean(PREF_WAKELOCK_ENABLED, value).apply()

    override fun onCreate() {
        super.onCreate()
        instance = this

        createNotificationChannel()
        Log.i(TAG, "Hermes Agent Android Companion initialized. Target Gateway: $gatewayUrl")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.service_notification_channel),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.service_notification_desc)
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
