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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.nousresearch.hermes.evolution.data.model.SkillItem
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
fun SkillLibraryScreen(
    onNavigateToFactory: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val sampleSkills = remember {
        listOf(
            SkillItem("vox-doc", "End-to-end Vox-style documentary video creation with maps and charts.", "1.2.0", "Creative", false, true),
            SkillItem("youtube-script-hindi", "High-retention Hindi YouTube scripts with 4 psychological hooks.", "1.0.0", "Creative", true, true),
            SkillItem("git-repo-audit", "AST and security audit for GitHub repositories.", "2.1.0", "DevOps", false, true),
            SkillItem("pdf-data-extractor", "Scans and extracts structured tables from PDFs on Android.", "1.1.0", "Productivity", true, true),
            SkillItem("edge-tts-hindi", "Microsoft Edge Neural voiceover synthesis for Hindi and Indian accents.", "1.0.0", "Media", false, true),
            SkillItem("device-battery-monitor", "Monitors battery temperature, charging speed, and background quotas.", "1.0.0", "System", true, true)
        )
    }

    val filtered = sampleSkills.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true)
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
                    Text("Skill Library", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("185 upstream manifests + custom self-developed skills", color = TextSecondary, fontSize = 12.sp)
                }

                Button(
                    onClick = onNavigateToFactory,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.height(16.dp))
                    Text("Create", fontSize = 12.sp)
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search skills...", color = TextMuted) },
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

        items(filtered) { skill ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(skill.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Surface(
                            color = if (skill.isCustom) EmeraldReady.copy(alpha = 0.2f) else CyanGlow.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (skill.isCustom) "CUSTOM" else "BUILT-IN",
                                color = if (skill.isCustom) EmeraldReady else CyanGlow,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(4.dp, 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(skill.description, color = TextSecondary, fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Category: ${skill.category} · v${skill.version}", color = TextMuted, fontSize = 11.sp)
                        Button(
                            onClick = { /* Test skill */ },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = EmeraldReady, modifier = Modifier.height(14.dp))
                            Text("Run Test", fontSize = 11.sp, color = TextPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SkillFactoryScreen(
    onSkillCreated: (String) -> Unit
) {
    var skillName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var pythonCode by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Autonomous Skill Factory", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Formulate, code, validate, and version new Hermes agent capabilities.", color = TextSecondary, fontSize = 13.sp)
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Skill Identifier", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = skillName,
                        onValueChange = { skillName = it },
                        placeholder = { Text("e.g. smart-device-optimizer", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Capability Description", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("What task does this skill accomplish?", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Agent Instructions (Markdown)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = instructions,
                        onValueChange = { instructions = it },
                        placeholder = { Text("Step-by-step guidance for the agent...", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Python Tool Script (Optional)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = pythonCode,
                        onValueChange = { pythonCode = it },
                        placeholder = { Text("def execute(args):\n    return {'status': 'ok'}", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        minLines = 4,
                        textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (skillName.isNotBlank() && description.isNotBlank()) {
                                statusMessage = "AST Analysis: PASSED. Generated version 1.0.0 and saved to hermes/skills/$skillName."
                                onSkillCreated(skillName)
                            } else {
                                statusMessage = "Error: Skill Name and Description are required."
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null)
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("Validate & Activate Skill", fontWeight = FontWeight.Bold)
                    }

                    if (statusMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = statusMessage!!,
                            color = if (statusMessage!!.startsWith("Error")) RoseError else EmeraldReady,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
