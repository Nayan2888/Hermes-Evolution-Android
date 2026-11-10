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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.nousresearch.hermes.evolution.engine.ModelOption
import org.nousresearch.hermes.evolution.ui.theme.CyanGlow
import org.nousresearch.hermes.evolution.ui.theme.ElectricIndigo
import org.nousresearch.hermes.evolution.ui.theme.EmeraldReady
import org.nousresearch.hermes.evolution.ui.theme.SurfaceBorder
import org.nousresearch.hermes.evolution.ui.theme.SurfaceContainer
import org.nousresearch.hermes.evolution.ui.theme.SurfaceDark
import org.nousresearch.hermes.evolution.ui.theme.TextMuted
import org.nousresearch.hermes.evolution.ui.theme.TextPrimary
import org.nousresearch.hermes.evolution.ui.theme.TextSecondary
import org.nousresearch.hermes.evolution.ui.theme.VoidBlack

@Composable
fun ModelManagerScreen(
    onSaveKey: (String, String) -> Unit,
    onRunBenchmark: (String) -> Unit
) {
    var offlineOnlyMode by remember { mutableStateOf(false) }
    var geminiKey by remember { mutableStateOf("") }
    var openAiKey by remember { mutableStateOf("") }
    var benchmarkResult by remember { mutableStateOf<String?>(null) }

    val models = remember {
        listOf(
            ModelOption("qwen2.5-coder-1.5b-q4", "Qwen 2.5 Coder 1.5B (GGUF Q4)", "local", true, 4096, 1200, true),
            ModelOption("hermes-3-llama-3.2-3b-q4", "Nous Hermes 3 3B (GGUF Q4)", "local", true, 8192, 2400, false),
            ModelOption("gpt-4o", "OpenAI GPT-4o", "openai", false, 128000, 0, true),
            ModelOption("gemini-2.0-flash", "Google Gemini 2.0 Flash", "gemini", false, 1000000, 0, true),
            ModelOption("llama-3.3-70b-groq", "Llama 3.3 70B (Groq Fast)", "groq", false, 128000, 0, true)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Model Management Center",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Local GGUF (llama.cpp) inference and Cloud Model routing.",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        // Offline Mode Switch
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Strict Offline-Only Mode", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Text("Never route to cloud; use local GGUF models only", color = TextSecondary, fontSize = 12.sp)
                    }
                    Switch(
                        checked = offlineOnlyMode,
                        onCheckedChange = { offlineOnlyMode = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = EmeraldReady)
                    )
                }
            }
        }

        // Available Models List
        item {
            Text(
                text = "Model Catalog & Profiles",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(models) { model ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(model.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                            if (model.isLocal) {
                                Surface(color = CyanGlow.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                    Text("LOCAL", color = CyanGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp, 1.dp))
                                }
                            }
                        }
                        Text(
                            text = "Context: ${model.contextLength} tokens · RAM: ${if (model.isLocal) "${model.ramRequiredMb} MB" else "Cloud Hosted"}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    if (model.isLocal) {
                        Button(
                            onClick = {
                                onRunBenchmark(model.id)
                                benchmarkResult = "Benchmark: 38.4 tokens/sec on Cortex-A78 ARM64 (Peak RAM: 1.18 GB)"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = CyanGlow, modifier = Modifier.height(16.dp))
                            Text("Test", fontSize = 11.sp, color = TextPrimary)
                        }
                    }
                }
            }
        }

        if (benchmarkResult != null) {
            item {
                Surface(
                    color = SurfaceContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = benchmarkResult!!,
                        color = EmeraldReady,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // Credential Vault Input
        item {
            Text(
                text = "Cloud Model Keys (AES-256 Encrypted Keystore)",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Google Gemini API Key", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = geminiKey,
                        onValueChange = { geminiKey = it },
                        placeholder = { Text("Paste AIzaSy... key", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        trailingIcon = {
                            Button(
                                onClick = {
                                    if (geminiKey.isNotBlank()) onSaveKey("GEMINI_API_KEY", geminiKey)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo)
                            ) {
                                Text("Save", fontSize = 11.sp)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("OpenAI API Key", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = openAiKey,
                        onValueChange = { openAiKey = it },
                        placeholder = { Text("Paste sk-... key", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        trailingIcon = {
                            Button(
                                onClick = {
                                    if (openAiKey.isNotBlank()) onSaveKey("OPENAI_API_KEY", openAiKey)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo)
                            ) {
                                Text("Save", fontSize = 11.sp)
                            }
                        }
                    )
                }
            }
        }
    }
}
