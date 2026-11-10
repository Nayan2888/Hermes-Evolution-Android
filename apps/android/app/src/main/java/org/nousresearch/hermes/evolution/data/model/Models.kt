package org.nousresearch.hermes.evolution.data.model

data class GatewayConfig(
    val url: String = "http://127.0.0.1:8080/",
    val authToken: String = "",
    val wakeLockEnabled: Boolean = false
)

enum class GatewayStatus {
    CONNECTING,
    CONNECTED,
    OFFLINE,
    ERROR
}
