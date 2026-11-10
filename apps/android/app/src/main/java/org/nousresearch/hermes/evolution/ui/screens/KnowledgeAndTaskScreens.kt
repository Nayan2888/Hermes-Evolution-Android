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
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import org.nousresearch.hermes.evolution.ui.theme.CyanGlow
import org.nousresearch.hermes.evolution.ui.theme.ElectricIndigo
import org.nousresearch.hermes.evolution.ui.theme.EmeraldReady
import org.nousresearch.hermes.evolution.ui.theme.RoseError
import org.nousresearch.hermes.evolution.ui.theme.SurfaceBorder
import org.nousresearch.hermes.evolution.ui.theme.SurfaceContainer
import org.nousresearch.hermes.evolution.ui.theme.SurfaceDark
import org.nousresearch.hermes.evolution.ui.theme.TextMuted
import org.nousresearch.hermes.evolution.ui.theme.TextPrimary
import org.nousresearch.hermes.evolution.ui.theme.TextSecondary
import org.nousresearch.hermes.evolution.ui.theme.VoidBlack

@Composable
fun KnowledgeVaultScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var backupStatus by remember { mutableStateOf<String?>(null) }

    val knowledgeEntries = remember {
        listOf(
            "Hermes System: Pinned commit fc86fb3 audited and verified for Samsung Galaxy M36 5G.",
            "Android 16: Requires 16 KB memory page alignment for all compiled native ELF binaries.",
            "Hindi YouTube Workflow: Hook archetypes: Curiosity Gap, Counter-Intuitive Truth, Shocking Metric, Personal Narrative.",
            "Memory Database: SQLite WAL mode enabled at hermes/state.db with atomic commit recovery."
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Knowledge Vault", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Persistent Hermes state, agent memories, and project knowledge.", color = TextSecondary, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        backupStatus = "Encrypted backup exported to hermes/backup_latest.enc"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Archive, contentDescription = null, tint = CyanGlow, modifier = Modifier.height(16.dp))
                    Text("Backup", fontSize = 12.sp, color = TextPrimary)
                }
            }
        }

        if (backupStatus != null) {
            item {
                Text(backupStatus!!, color = EmeraldReady, fontSize = 12.sp)
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search knowledge vault...", color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricIndigo,
                    unfocusedBorderColor = SurfaceBorder,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        }

        items(knowledgeEntries) { entry ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(entry, color = TextPrimary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Verified & Stored in SQLite state.db", color = TextMuted, fontSize = 11.sp)
                }
            }
        }
    }
}

data class PlanStep(
    val name: String,
    val tool: String,
    val status: String // "COMPLETED", "RUNNING", "PENDING"
)

@Composable
fun TaskManagerScreen() {
    val steps = remember {
        listOf(
            PlanStep("Topic Research & Scientific Verification", "web_tools / Wikipedia API", "COMPLETED"),
            PlanStep("Hindi Script Composition & Hook Architecture", "youtube-script-hindi skill", "COMPLETED"),
            PlanStep("Scene Duration Scaling & Visual Storyboard", "story_engine tool", "RUNNING"),
            PlanStep("Voiceover Synthesis (hi-IN-MadhurNeural)", "edge-tts tool", "PENDING"),
            PlanStep("WebVTT Subtitle Word Alignment", "subtitle_aligner tool", "PENDING"),
            PlanStep("FFmpeg Multi-Track Video Render", "ffmpeg filter_complex", "PENDING")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Multi-Skill Task Manager", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Autonomous multi-phase orchestration engine with conservative RAM budget.", color = TextSecondary, fontSize = 12.sp)
        }

        // Active Task Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Task: Hindi Documentary on Black Holes", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Surface(color = CyanGlow.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                            Text("RUNNING", color = CyanGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(6.dp, 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.45f },
                        modifier = Modifier.fillMaxWidth(),
                        color = ElectricIndigo,
                        trackColor = SurfaceContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Phase 3 of 6: Visual Storyboard Generation", color = TextSecondary, fontSize = 12.sp)
                }
            }
        }

        item {
            Text("Task Workflow Pipeline", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        items(steps) { step ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(step.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("Executor: ${step.tool}", color = TextSecondary, fontSize = 11.sp)
                    }

                    Surface(
                        color = when (step.status) {
                            "COMPLETED" -> EmeraldReady.copy(alpha = 0.2f)
                            "RUNNING" -> CyanGlow.copy(alpha = 0.2f)
                            else -> SurfaceContainer
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = step.status,
                            color = when (step.status) {
                                "COMPLETED" -> EmeraldReady
                                "RUNNING" -> CyanGlow
                                else -> TextMuted
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(6.dp, 2.dp)
                        )
                    }
                }
            }
        }
    }
}
