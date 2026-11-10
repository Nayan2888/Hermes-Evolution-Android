package org.nousresearch.hermes.evolution.engine

import android.content.Context
import android.util.Log
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * Singleton IPC bridge coordinating Kotlin flows and Embedded Python Hermes runtime.
 */
class HermesBridge private constructor(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val gson = Gson()

    private val _agentState = MutableStateFlow(AgentState.READY)
    val agentState: StateFlow<AgentState> = _agentState.asStateFlow()

    private val _events = MutableSharedFlow<HermesEvent>(replay = 50)
    val events: SharedFlow<HermesEvent> = _events.asSharedFlow()

    private var pyBridgeInstance: PyObject? = null
    private var isInitialized = false

    init {
        initializePythonBridge()
    }

    private fun initializePythonBridge() {
        try {
            if (!Python.isStarted()) {
                Log.w(TAG, "Python not started yet; bridge waiting.")
                return
            }
            val py = Python.getInstance()
            val module = py.getModule("hermes_android.hermes_android_bridge")
            val filesDir = context.filesDir.absolutePath
            val cacheDir = context.cacheDir.absolutePath

            pyBridgeInstance = module.callAttr("HermesAndroidBridge", filesDir, cacheDir)

            // Register event callback from Python to Kotlin
            val callback = object {
                @Suppress("unused")
                fun onEvent(jsonString: String) {
                    handleJsonEvent(jsonString)
                }
            }
            pyBridgeInstance?.callAttr("set_event_callback", callback)
            isInitialized = true
            Log.i(TAG, "HermesAndroidBridge linked with Chaquopy runtime.")
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to initialize embedded Python bridge: ${e.message}", e)
            _agentState.value = AgentState.OFFLINE
        }
    }

    fun handleJsonEvent(jsonString: String) {
        scope.launch {
            try {
                val mapType = object : TypeToken<Map<String, Any?>>() {}.type
                val root: Map<String, Any?> = gson.fromJson(jsonString, mapType)
                val type = root["type"] as? String ?: return@launch
                val sessionId = root["session_id"] as? String
                val data = root["data"] as? Map<String, Any?> ?: emptyMap()

                when (type) {
                    "status_changed" -> {
                        val statusStr = data["status"] as? String ?: "READY"
                        val newState = try {
                            AgentState.valueOf(statusStr)
                        } catch (e: Exception) {
                            AgentState.READY
                        }
                        _agentState.value = newState
                        _events.emit(HermesEvent.StatusChanged(newState, sessionId))
                    }
                    "thinking_delta" -> {
                        val text = data["text"] as? String ?: ""
                        _events.emit(HermesEvent.ThinkingDelta(text))
                    }
                    "token_delta" -> {
                        val token = data["token"] as? String ?: ""
                        _events.emit(HermesEvent.TokenDelta(token))
                    }
                    "tool_call" -> {
                        val callId = data["call_id"] as? String ?: ""
                        val toolName = data["tool_name"] as? String ?: ""
                        val args = data["arguments"] as? Map<String, Any?> ?: emptyMap()
                        val risk = data["risk_level"] as? String ?: "LOW"
                        val reqApproval = data["requires_approval"] as? Boolean ?: false
                        _events.emit(HermesEvent.ToolCall(callId, toolName, args, risk, reqApproval))
                    }
                    "tool_approval_required" -> {
                        val callId = data["call_id"] as? String ?: ""
                        val toolName = data["tool_name"] as? String ?: ""
                        val args = data["arguments"] as? Map<String, Any?> ?: emptyMap()
                        val risk = data["risk_level"] as? String ?: "HIGH"
                        _agentState.value = AgentState.WAITING
                        _events.emit(HermesEvent.ToolApprovalRequired(callId, toolName, args, risk))
                    }
                    "tool_result" -> {
                        val callId = data["call_id"] as? String ?: ""
                        val output = data["output"] as? String ?: ""
                        val isError = data["is_error"] as? Boolean ?: false
                        _events.emit(HermesEvent.ToolResult(callId, output, isError))
                    }
                    "turn_completed" -> {
                        val response = data["response"] as? String ?: ""
                        _events.emit(HermesEvent.TurnCompleted(response, sessionId))
                    }
                    "turn_cancelled" -> {
                        val msg = data["message"] as? String ?: "Turn cancelled"
                        _events.emit(HermesEvent.TurnCancelled(msg))
                    }
                    "error" -> {
                        val err = data["error_message"] as? String ?: "Unknown error"
                        val errType = data["type"] as? String
                        _events.emit(HermesEvent.Error(err, errType))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing event from Python bridge: ${e.message}", e)
            }
        }
    }

    fun startSession(sessionId: String? = null, workspace: String = "default"): String {
        return try {
            pyBridgeInstance?.callAttr("start_session", sessionId, workspace)?.toString()
                ?: (sessionId ?: java.util.UUID.randomUUID().toString())
        } catch (e: Exception) {
            Log.e(TAG, "Error starting session: ${e.message}", e)
            sessionId ?: java.util.UUID.randomUUID().toString()
        }
    }

    fun processMessage(
        message: String,
        modelName: String = "qwen2.5-coder-1.5b",
        provider: String = "local",
        apiKey: String? = null,
        baseUrl: String? = null
    ) {
        scope.launch {
            try {
                if (pyBridgeInstance != null) {
                    pyBridgeInstance?.callAttr("process_message", message, modelName, provider, apiKey, baseUrl)
                } else {
                    // Fallback local processing
                    _agentState.value = AgentState.THINKING
                    _events.emit(HermesEvent.ThinkingDelta("Running local standalone Hermes handler...\n"))
                    _agentState.value = AgentState.WORKING
                    _events.emit(HermesEvent.TokenDelta("Hermes Evolution [Standalone]: $message"))
                    _agentState.value = AgentState.COMPLETED
                    _events.emit(HermesEvent.TurnCompleted("Completed local execution.", null))
                }
            } catch (e: Exception) {
                Log.e(TAG, "processMessage execution error: ${e.message}", e)
                _agentState.value = AgentState.ERROR
                _events.emit(HermesEvent.Error(e.message ?: "Execution error", e.javaClass.simpleName))
            }
        }
    }

    fun submitToolApproval(callId: String, approved: Boolean) {
        scope.launch {
            try {
                pyBridgeInstance?.callAttr("submit_tool_approval", callId, approved)
            } catch (e: Exception) {
                Log.e(TAG, "Error submitting approval: ${e.message}", e)
            }
        }
    }

    fun cancelTurn() {
        scope.launch {
            try {
                pyBridgeInstance?.callAttr("cancel_turn")
            } catch (e: Exception) {
                Log.e(TAG, "Error cancelling turn: ${e.message}", e)
            }
        }
    }

    companion object {
        private const val TAG = "HermesBridge"

        @Volatile
        private var instance: HermesBridge? = null

        fun getInstance(context: Context): HermesBridge {
            return instance ?: synchronized(this) {
                instance ?: HermesBridge(context.applicationContext).also { instance = it }
            }
        }
    }
}
