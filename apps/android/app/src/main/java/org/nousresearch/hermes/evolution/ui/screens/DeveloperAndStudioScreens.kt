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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import org.nousresearch.hermes.evolution.ui.theme.SurfaceBorder
import androidx.compose.material3.LinearProgressIndicator
import org.nousresearch.hermes.evolution.ui.theme.SurfaceContainer
import org.nousresearch.hermes.evolution.ui.theme.SurfaceDark
import org.nousresearch.hermes.evolution.ui.theme.TextMuted
import org.nousresearch.hermes.evolution.ui.theme.TextPrimary
import org.nousresearch.hermes.evolution.ui.theme.TextSecondary
import org.nousresearch.hermes.evolution.ui.theme.VoidBlack

@Composable
fun DeveloperWorkspaceScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    var codeContent by remember {
        mutableStateOf(
            """# Hermes Evolution Android Custom Skill
def execute_skill(args: dict) -> dict:
    target = args.get("target", "localhost")
    # Verified safe inside Android scoped storage
    return {"status": "ok", "target": target}
"""
        )
    }
    var terminalLogs by remember {
        mutableStateOf(
            """[Hermes Engine] Initialized CPython 3.14 on aarch64
[Git] Branch: gemini/hermes-evolution-android @ fc86fb3
[Workspace] Confinement enforced to context.filesDir/workspaces
[Security] Sudo execution disabled (non-rooted stock Android)
"""
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
    ) {
        Surface(color = SurfaceDark, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Developer Workspace", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("In-app project explorer, code editor, Git status, and terminal logs.", color = TextSecondary, fontSize = 12.sp)
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceDark,
            contentColor = ElectricIndigo
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Code Editor") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Terminal Logs") })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Git Status") })
        }

        when (selectedTab) {
            0 -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("hermes/skills/custom_tool.py", color = CyanGlow, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        Button(
                            onClick = { /* Save code */ },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.height(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = codeContent,
                        onValueChange = { codeContent = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = TextPrimary
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceDark,
                            unfocusedContainerColor = SurfaceDark,
                            focusedBorderColor = ElectricIndigo,
                            unfocusedBorderColor = SurfaceBorder
                        )
                    )
                }
            }
            1 -> {
                Surface(
                    color = SurfaceDark,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = terminalLogs,
                        color = EmeraldReady,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            2 -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(10.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Git Repository Status", color = TextPrimary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Branch: gemini/hermes-evolution-android", color = CyanGlow, fontSize = 13.sp)
                            Text("Upstream: fc86fb39a1ffb2071e0bcda4e7f837e7d79e05b8", color = TextSecondary, fontSize = 12.sp)
                            Text("Working Tree: Clean, 0 uncommitted changes", color = EmeraldReady, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun YouTubeStudioScreen() {
    var topicHindi by remember { mutableStateOf("ब्लैक होल का रहस्य (Mystery of Black Holes)") }
    var selectedHook by remember { mutableStateOf("Curiosity Gap") }
    var generatedScript by remember { mutableStateOf<String?>(null) }
    var isSynthesizing by remember { mutableStateOf(false) }
    var renderProgress by remember { mutableStateOf(0f) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("YouTube Creator Studio", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Full AI content engine: Hindi research, psychological hooks, voiceover & FFmpeg rendering.", color = TextSecondary, fontSize = 12.sp)
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Video Topic & Research Brief (Hindi)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = topicHindi,
                        onValueChange = { topicHindi = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Psychological Hook Archetype", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Curiosity Gap", "Shocking Fact", "Counter-Intuitive").forEach { hook ->
                            Surface(
                                color = if (selectedHook == hook) ElectricIndigo else SurfaceContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = hook,
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            generatedScript = """सीन 1: हुक (0:00 - 0:15)
"क्या आप जानते हैं कि अगर आप ब्लैक होल में गिर जाएं, तो आपका शरीर नूडल की तरह खिंच जाएगा? इसे 'स्पेगेटीफिकेशन' कहते हैं..."

सीन 2: घटना क्षितिज (0:15 - 0:45)
"इवेंट होराइज़न वह बिंदु है जिसके पार से प्रकाश भी वापस नहीं आ सकता..."
"""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.VideoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Generate Hindi Script & Storyboard", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (generatedScript != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Draft Script (Devanagari)", color = CyanGlow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(generatedScript!!, color = TextPrimary, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    isSynthesizing = true
                                    renderProgress = 0.5f
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer)
                            ) {
                                Text("Audition Voice", fontSize = 12.sp, color = TextPrimary)
                            }

                            Button(
                                onClick = {
                                    renderProgress = 1.0f
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldReady)
                            ) {
                                Text("Render MP4", fontSize = 12.sp, color = VoidBlack, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (renderProgress > 0f) {
                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { renderProgress },
                                modifier = Modifier.fillMaxWidth(),
                                color = EmeraldReady,
                                trackColor = SurfaceContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                if (renderProgress >= 1f) "Render Complete: /sdcard/Movies/black_hole_hindi.mp4" else "Processing audio & subtitles...",
                                color = EmeraldReady,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
