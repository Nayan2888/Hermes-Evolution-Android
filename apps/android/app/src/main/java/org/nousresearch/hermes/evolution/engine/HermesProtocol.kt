package org.nousresearch.hermes.evolution.engine

enum class AgentState {
    READY,
    THINKING,
    WORKING,
    WAITING,
    COMPLETED,
    ERROR,
    OFFLINE
}

sealed class HermesEvent {
    data class StatusChanged(val state: AgentState, val sessionId: String?) : HermesEvent()
    data class ThinkingDelta(val text: String) : HermesEvent()
    data class TokenDelta(val token: String) : HermesEvent()
    data class ToolCall(
        val callId: String,
        val toolName: String,
        val arguments: Map<String, Any?>,
        val riskLevel: String,
        val requiresApproval: Boolean
    ) : HermesEvent()
    data class ToolApprovalRequired(
        val callId: String,
        val toolName: String,
        val arguments: Map<String, Any?>,
        val riskLevel: String
    ) : HermesEvent()
    data class ToolResult(
        val callId: String,
        val output: String,
        val isError: Boolean
    ) : HermesEvent()
    data class TurnCompleted(
        val response: String,
        val sessionId: String?
    ) : HermesEvent()
    data class TurnCancelled(val message: String) : HermesEvent()
    data class Error(val message: String, val type: String?) : HermesEvent()
}

sealed class HermesCommand {
    data class StartSession(val sessionId: String?, val workspaceName: String = "default") : HermesCommand()
    data class ProcessMessage(
        val text: String,
        val modelName: String = "qwen2.5-coder-1.5b",
        val provider: String = "local",
        val apiKey: String? = null,
        val baseUrl: String? = null
    ) : HermesCommand()
    data class SubmitToolApproval(val callId: String, val approved: Boolean) : HermesCommand()
    object CancelTurn : HermesCommand()
}
