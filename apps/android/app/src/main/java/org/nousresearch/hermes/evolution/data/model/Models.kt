package org.nousresearch.hermes.evolution.data.model

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user", "agent", "tool", "system"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val thinking: String? = null,
    val toolCallId: String? = null,
    val toolName: String? = null,
    val isError: Boolean = false
)

data class SkillItem(
    val name: String,
    val description: String,
    val version: String = "1.0.0",
    val category: String = "General",
    val isCustom: Boolean = false,
    val hasScript: Boolean = false,
    val path: String = "",
    val status: String = "READY"
)

data class EvolutionRecord(
    val skillName: String,
    val version: String,
    val testDate: Long,
    val passed: Boolean,
    val baselineScore: Double,
    val improvedScore: Double,
    val errorDetails: String? = null,
    val canRollback: Boolean = true
)

data class YouTubeProject(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val topicHindi: String,
    val hookType: String = "Curiosity Gap",
    val targetDurationMinutes: Double = 5.0,
    val scriptDraft: String = "",
    val isAudioReady: Boolean = false,
    val isSubtitlesReady: Boolean = false,
    val isVideoReady: Boolean = false,
    val renderOutputPath: String? = null
)

data class RemoteWorkerConfig(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val endpointUrl: String,
    val authToken: String = "",
    val capabilities: List<String> = listOf("ANDROID_BUILD", "LARGE_MODEL_INFERENCE"),
    val isConnected: Boolean = false
)

data class AutomationTask(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val intervalHours: Int = 24,
    val taskPrompt: String,
    val lastRunTimestamp: Long = 0L,
    val isEnabled: Boolean = true
)
