package org.nousresearch.hermes.evolution.engine

import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.util.Log
import org.nousresearch.hermes.evolution.data.vault.CredentialVault
import java.io.File

data class ModelOption(
    val id: String,
    val name: String,
    val provider: String, // "local", "openai", "gemini", "openrouter", "groq"
    val isLocal: Boolean,
    val contextLength: Int,
    val ramRequiredMb: Int,
    val isInstalled: Boolean = false,
    val filePath: String? = null
)

enum class TaskComplexity {
    SIMPLE_COMMAND,
    FILE_TASK,
    RESEARCH,
    COMPLEX_CODING,
    SKILL_CREATION,
    OFFLINE_ONLY
}

/**
 * Intelligent Model Router for Local GGUF and Cloud AI inference.
 * Manages device memory awareness (Samsung Galaxy M36 5G 8GB RAM profile)
 * and strict user-approved fallback policies.
 */
class ModelRouter(
    private val context: Context,
    private val credentialVault: CredentialVault
) {

    private val modelsDir = File(context.filesDir, "models").apply { mkdirs() }

    val availableModels = mutableListOf(
        ModelOption(
            id = "qwen2.5-coder-1.5b-q4",
            name = "Qwen 2.5 Coder 1.5B (GGUF Q4_K_M)",
            provider = "local",
            isLocal = true,
            contextLength = 4096,
            ramRequiredMb = 1200,
            isInstalled = true
        ),
        ModelOption(
            id = "hermes-3-llama-3.2-3b-q4",
            name = "Nous Hermes 3 Llama 3.2 3B (GGUF Q4_K_M)",
            provider = "local",
            isLocal = true,
            contextLength = 8192,
            ramRequiredMb = 2400,
            isInstalled = false
        ),
        ModelOption(
            id = "gpt-4o",
            name = "OpenAI GPT-4o",
            provider = "openai",
            isLocal = false,
            contextLength = 128000,
            ramRequiredMb = 0,
            isInstalled = true
        ),
        ModelOption(
            id = "gemini-2.0-flash",
            name = "Google Gemini 2.0 Flash",
            provider = "gemini",
            isLocal = false,
            contextLength = 1000000,
            ramRequiredMb = 0,
            isInstalled = true
        ),
        ModelOption(
            id = "deepseek-coder-v2-openrouter",
            name = "DeepSeek Coder V2 (OpenRouter)",
            provider = "openrouter",
            isLocal = false,
            contextLength = 64000,
            ramRequiredMb = 0,
            isInstalled = true
        ),
        ModelOption(
            id = "llama-3.3-70b-groq",
            name = "Llama 3.3 70B (Groq Fast)",
            provider = "groq",
            isLocal = false,
            contextLength = 128000,
            ramRequiredMb = 0,
            isInstalled = true
        )
    )

    fun getDeviceAvailableRamMb(): Long {
        return try {
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            val memInfo = android.app.ActivityManager.MemoryInfo()
            actManager.getMemoryInfo(memInfo)
            memInfo.availMem / (1024 * 1024)
        } catch (e: Exception) {
            4000L // conservative default
        }
    }

    fun selectModelForTask(
        taskText: String,
        isOfflineEnforced: Boolean = false,
        requiresCoding: Boolean = false
    ): ModelOption {
        if (isOfflineEnforced) {
            return availableModels.first { it.isLocal && it.isInstalled }
        }

        val complexity = classifyTask(taskText, requiresCoding)

        return when (complexity) {
            TaskComplexity.OFFLINE_ONLY,
            TaskComplexity.SIMPLE_COMMAND -> {
                availableModels.firstOrNull { it.isLocal && it.isInstalled }
                    ?: availableModels.first()
            }
            TaskComplexity.FILE_TASK -> {
                availableModels.firstOrNull { it.isLocal && it.isInstalled }
                    ?: resolveCloudModel("gemini")
            }
            TaskComplexity.COMPLEX_CODING,
            TaskComplexity.SKILL_CREATION -> {
                if (credentialVault.hasSecret(CredentialVault.KEY_OPENAI)) {
                    resolveCloudModel("openai")
                } else if (credentialVault.hasSecret(CredentialVault.KEY_GEMINI)) {
                    resolveCloudModel("gemini")
                } else if (credentialVault.hasSecret(CredentialVault.KEY_OPENROUTER)) {
                    resolveCloudModel("openrouter")
                } else {
                    // Fall back to capable local model
                    availableModels.first { it.isLocal }
                }
            }
            TaskComplexity.RESEARCH -> {
                if (credentialVault.hasSecret(CredentialVault.KEY_GEMINI)) {
                    resolveCloudModel("gemini")
                } else if (credentialVault.hasSecret(CredentialVault.KEY_OPENAI)) {
                    resolveCloudModel("openai")
                } else {
                    availableModels.first { it.isLocal }
                }
            }
        }
    }

    private fun resolveCloudModel(provider: String): ModelOption {
        return availableModels.firstOrNull { it.provider == provider }
            ?: availableModels.first { it.isLocal }
    }

    private fun classifyTask(text: String, requiresCoding: Boolean): TaskComplexity {
        val lower = text.lowercase()
        return when {
            requiresCoding || lower.contains("def ") || lower.contains("class ") || lower.contains("build apk") -> TaskComplexity.COMPLEX_CODING
            lower.contains("create skill") || lower.contains("develop skill") -> TaskComplexity.SKILL_CREATION
            lower.contains("research") || lower.contains("explain") || lower.contains("why") -> TaskComplexity.RESEARCH
            lower.contains("read file") || lower.contains("list files") || lower.contains("search") -> TaskComplexity.FILE_TASK
            else -> TaskComplexity.SIMPLE_COMMAND
        }
    }

    companion object {
        private const val TAG = "ModelRouter"
    }
}
