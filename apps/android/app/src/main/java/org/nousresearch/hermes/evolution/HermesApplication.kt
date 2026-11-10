package org.nousresearch.hermes.evolution

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import org.nousresearch.hermes.evolution.data.vault.CredentialVault

class HermesApplication : Application() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "hermes_evolution_agent_channel"
        private const val TAG = "HermesApplication"
        lateinit var instance: HermesApplication
            private set
    }

    lateinit var credentialVault: CredentialVault
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize Android Keystore backed Credential Vault
        credentialVault = CredentialVault(this)

        // Initialize Notification Channel for Foreground Service
        createNotificationChannel()

        // Initialize Python runtime via Chaquopy if available
        initPythonRuntime()

        Log.i(TAG, "Hermes Evolution Android Application initialized.")
    }

    private fun initPythonRuntime() {
        try {
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(this))
                Log.i(TAG, "Chaquopy embedded Python runtime started successfully.")
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Chaquopy startup deferred or running in host test mode: ${e.message}")
        }
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
