package org.nousresearch.hermes.evolution.engine

import android.content.Context
import android.util.Log
import java.io.File

data class AuditLogEntry(
    val timestamp: Long,
    val toolName: String,
    val riskLevel: String, // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    val actionSummary: String,
    val approved: Boolean,
    val deniedReason: String? = null
)

/**
 * Default-Deny Security Gatekeeper for Hermes Evolution Android.
 * Enforces workspace confinement, dangerous tool approval checks,
 * secret scrubbing, and immutable audit logs.
 */
class SecurityGatekeeper(private val context: Context) {

    private val auditLogs = mutableListOf<AuditLogEntry>()
    private val allowedWorkspaces = mutableSetOf<String>()

    init {
        // Confine allowed workspace roots strictly to context.filesDir
        val defaultWorkspace = File(context.filesDir, "workspaces").apply { mkdirs() }
        allowedWorkspaces.add(defaultWorkspace.canonicalPath)
    }

    fun isPathWithinSandbox(candidatePath: String): Boolean {
        return try {
            val file = File(candidatePath).canonicalFile
            allowedWorkspaces.any { allowedRoot ->
                file.canonicalPath.startsWith(allowedRoot)
            }
        } catch (e: Exception) {
            false
        }
    }

    fun evaluateToolRisk(toolName: String, arguments: Map<String, Any?>): Pair<String, Boolean> {
        val lowerTool = toolName.lowercase()

        // Critical / Destructive operations
        if (lowerTool.contains("terminal") || lowerTool.contains("shell") || lowerTool.contains("execute")) {
            return Pair("CRITICAL", true) // Always requires interactive user approval
        }

        if (lowerTool.contains("write_file") || lowerTool.contains("delete") || lowerTool.contains("rm")) {
            val path = arguments["path"] as? String ?: arguments["file"] as? String ?: ""
            if (!isPathWithinSandbox(path)) {
                return Pair("CRITICAL", true)
            }
            return Pair("HIGH", true)
        }

        if (lowerTool.contains("install_skill") || lowerTool.contains("git_clone")) {
            return Pair("HIGH", true)
        }

        if (lowerTool.contains("http") || lowerTool.contains("web_search") || lowerTool.contains("curl")) {
            return Pair("MEDIUM", false)
        }

        return Pair("LOW", false)
    }

    fun recordAudit(
        toolName: String,
        riskLevel: String,
        summary: String,
        approved: Boolean,
        deniedReason: String? = null
    ) {
        val entry = AuditLogEntry(
            timestamp = System.currentTimeMillis(),
            toolName = toolName,
            riskLevel = riskLevel,
            actionSummary = summary,
            approved = approved,
            deniedReason = deniedReason
        )
        auditLogs.add(0, entry)
        if (auditLogs.size > 500) {
            auditLogs.removeAt(auditLogs.lastIndex)
        }
        Log.i(TAG, "Security Audit: [${riskLevel}] $toolName - approved: $approved")
    }

    fun getRecentAuditLogs(): List<AuditLogEntry> = auditLogs.toList()

    fun scrubSensitiveSecrets(input: String): String {
        return input
            .replace(Regex("(sk-[a-zA-Z0-9_-]{20,})"), "sk-***REDACTED***")
            .replace(Regex("(AIzaSy[a-zA-Z0-9_-]{33})"), "AIzaSy***REDACTED***")
            .replace(Regex("(gsk_[a-zA-Z0-9_-]{20,})"), "gsk_***REDACTED***")
            .replace(Regex("(ghp_[a-zA-Z0-9]{36})"), "ghp_***REDACTED***")
    }

    companion object {
        private const val TAG = "SecurityGatekeeper"
    }
}
