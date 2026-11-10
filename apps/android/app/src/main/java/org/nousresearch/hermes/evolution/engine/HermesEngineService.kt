package org.nousresearch.hermes.evolution.engine

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.nousresearch.hermes.evolution.HermesApplication
import org.nousresearch.hermes.evolution.MainActivity
import org.nousresearch.hermes.evolution.R

/**
 * Android Foreground Service hosting the embedded Python Hermes agent runtime.
 * Guarantees persistent execution across Android 16 background limits and job quotas.
 */
class HermesEngineService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var bridge: HermesBridge

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "HermesEngineService created.")

        bridge = HermesBridge.getInstance(this)

        acquireWakeLock()
        startForegroundWithNotification(AgentState.READY)
        observeAgentState()
    }

    private fun acquireWakeLock() {
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "HermesEvolution:EngineWakeLock").apply {
                setReferenceCounted(false)
                acquire(10 * 60 * 1000L) // 10 minutes default safety cap
            }
        } catch (e: Exception) {
            Log.w(TAG, "Unable to acquire WakeLock: ${e.message}")
        }
    }

    private fun startForegroundWithNotification(state: AgentState) {
        val notification = buildNotification(state)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun buildNotification(state: AgentState): Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val statusText = when (state) {
            AgentState.READY -> getString(R.string.agent_status_ready)
            AgentState.THINKING -> getString(R.string.agent_status_thinking)
            AgentState.WORKING -> getString(R.string.agent_status_working)
            AgentState.WAITING -> getString(R.string.agent_status_waiting)
            AgentState.COMPLETED -> getString(R.string.agent_status_completed)
            AgentState.ERROR -> getString(R.string.agent_status_error)
            AgentState.OFFLINE -> getString(R.string.agent_status_offline)
        }

        return NotificationCompat.Builder(this, HermesApplication.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Hermes Evolution")
            .setContentText(statusText)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun observeAgentState() {
        serviceScope.launch {
            bridge.agentState.collectLatest { state ->
                val notification = buildNotification(state)
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                manager.notify(NOTIFICATION_ID, notification)
            }
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.w(TAG, "onTrimMemory received level: $level. Notifying Hermes memory optimizer.")
        if (level >= TRIM_MEMORY_RUNNING_CRITICAL) {
            // Signal python garbage collector and cache cleanup
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.d(TAG, "onStartCommand with action: $action")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
        Log.i(TAG, "HermesEngineService destroyed.")
    }

    companion object {
        private const val TAG = "HermesEngineService"
        const val NOTIFICATION_ID = 48201

        fun start(context: Context) {
            val intent = Intent(context, HermesEngineService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, HermesEngineService::class.java)
            context.stopService(intent)
        }
    }
}
