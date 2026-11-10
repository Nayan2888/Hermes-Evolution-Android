package org.nousresearch.hermes.evolution.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.nousresearch.hermes.evolution.ui.theme.CyanGlow
import org.nousresearch.hermes.evolution.ui.theme.ElectricIndigo
import org.nousresearch.hermes.evolution.ui.theme.EmeraldReady
import org.nousresearch.hermes.evolution.ui.theme.RoseError
import org.nousresearch.hermes.evolution.ui.theme.SurfaceContainer
import org.nousresearch.hermes.evolution.ui.theme.SurfaceDark
import org.nousresearch.hermes.evolution.ui.theme.TextMuted
import org.nousresearch.hermes.evolution.ui.theme.TextPrimary
import org.nousresearch.hermes.evolution.ui.theme.TextSecondary
import org.nousresearch.hermes.evolution.ui.theme.VoidBlack

@Composable
fun AndroidToolsScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Android System Tools", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Native device integrations: Scoped storage, Share sheet, Notifications & Sensors.", color = TextSecondary, fontSize = 12.sp)
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(10.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Storage Confinement", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("App files: /data/user/0/org.nousresearch.hermes.evolution/files/hermes", color = TextSecondary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { /* Open picker */ },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer)
                    ) {
                        Text("Open Document Picker", color = TextPrimary)
                    }
                }
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(10.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Share Intent & Notifications", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Send generated artifacts to other apps or post status updates.", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { /* Share */ },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.height(16.dp))
                            Text("Share File")
                        }
                        Button(
                            onClick = { /* Notify */ },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer)
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = CyanGlow, modifier = Modifier.height(16.dp))
                            Text("Test Notification", color = TextPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AutomationsScreen() {
    var isEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Automations & WorkManager", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Periodic background execution compliant with Android 16 quotas.", color = TextSecondary, fontSize = 12.sp)
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Daily Knowledge Vault Sync", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Text("Runs every 24 hours while device is idle & charging", color = TextSecondary, fontSize = 12.sp)
                    }
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { isEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = EmeraldReady)
                    )
                }
            }
        }
    }
}

@Composable
fun RemoteWorkersScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Remote Workers Hub", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Optional authenticated workers for heavy tasks (Android builds, large LLMs).", color = TextSecondary, fontSize = 12.sp)
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(10.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("GitHub Actions Build Worker", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Surface(color = EmeraldReady.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                            Text("CONFIGURED", color = EmeraldReady, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp, 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Role: Compiles signed ARM64 APKs via .github/workflows/android-build.yml", color = TextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ActivityLogsScreen() {
    val logs = remember {
        listOf(
            "[SECURITY] Default-deny gatekeeper loaded. Path traversal protection active.",
            "[ENGINE] Embedded CPython 3.14 initialized inside HermesEngineService.",
            "[MODEL] Local Qwen 2.5 Coder 1.5B GGUF ready for offline execution.",
            "[MEMORY] SQLite state.db opened in WAL mode with atomic commit recovery."
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Real-Time Activity & Audit Logs", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Live stream of agent events, tool calls, and redacted secrets.", color = TextSecondary, fontSize = 12.sp)
        }

        items(logs) { log ->
            Surface(
                color = SurfaceDark,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = log,
                    color = if (log.contains("SECURITY")) CyanGlow else TextPrimary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun SecurityScreen() {
    var defaultDenyActive by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Security & Approvals Gatekeeper", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Default-deny security model and capability authorization boundaries.", color = TextSecondary, fontSize = 12.sp)
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Default-Deny Tool Gate", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Text("Require interactive modal approval before executing write or shell tools", color = TextSecondary, fontSize = 12.sp)
                    }
                    Switch(
                        checked = defaultDenyActive,
                        onCheckedChange = { defaultDenyActive = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = EmeraldReady)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Settings & Diagnostics", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Engine preferences, storage maintenance, and self-diagnostics.", color = TextSecondary, fontSize = 12.sp)
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(10.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Hermes Evolution Version", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("v1.0.0-evolution (Upstream pinned commit fc86fb3)", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { /* Clear cache */ },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = RoseError, modifier = Modifier.height(16.dp))
                        Text("Clear Temporary Cache", color = TextPrimary)
                    }
                }
            }
        }
    }
}
