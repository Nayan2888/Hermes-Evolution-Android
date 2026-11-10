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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import org.nousresearch.hermes.evolution.engine.AgentState
import org.nousresearch.hermes.evolution.ui.components.AgentOrb
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
fun DashboardScreen(
    agentState: AgentState,
    onNavigateToChat: (String?) -> Unit,
    onNavigateToYouTube: () -> Unit,
    onNavigateToGitHub: () -> Unit,
    onNavigateToSkillFactory: () -> Unit
) {
    var quickPrompt by remember { mutableStateOf("") }

    val statusText = when (agentState) {
        AgentState.READY -> "HERMES EVOLUTION — READY TO WORK"
        AgentState.THINKING -> "HERMES EVOLUTION — THINKING"
        AgentState.WORKING -> "HERMES EVOLUTION — EXECUTING TOOLS"
        AgentState.WAITING -> "HERMES EVOLUTION — AWAITING USER APPROVAL"
        AgentState.COMPLETED -> "HERMES EVOLUTION — TASK COMPLETED"
        AgentState.ERROR -> "HERMES EVOLUTION — ATTENTION REQUIRED"
        AgentState.OFFLINE -> "HERMES EVOLUTION — LOCAL OFFLINE MODE"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            AgentOrb(state = agentState, size = 180.dp)
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = statusText,
                color = when (agentState) {
                    AgentState.READY, AgentState.COMPLETED -> EmeraldReady
                    AgentState.THINKING, AgentState.WORKING -> CyanGlow
                    else -> ElectricIndigo
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Prompt Bar
            OutlinedTextField(
                value = quickPrompt,
                onValueChange = { quickPrompt = it },
                placeholder = { Text("Ask Hermes or enter a mission...", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (quickPrompt.isNotBlank()) {
                                onNavigateToChat(quickPrompt)
                                quickPrompt = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = ElectricIndigo)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricIndigo,
                    unfocusedBorderColor = SurfaceBorder,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // System Telemetry Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Target Hardware", color = TextSecondary, fontSize = 11.sp)
                        Text("Samsung M36 5G", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("RAM Profile", color = TextSecondary, fontSize = 11.sp)
                        Text("8 GB ARM64", color = EmeraldReady, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Python Runtime", color = TextSecondary, fontSize = 11.sp)
                        Text("CPython 3.14", color = CyanGlow, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Action Hub
            Text(
                text = "Autonomous Studios & Tools",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onNavigateToChat(null) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = ElectricIndigo)
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text("Chat", color = TextPrimary, fontSize = 13.sp)
                }

                Button(
                    onClick = onNavigateToYouTube,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer)
                ) {
                    Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = CyanGlow)
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text("YouTube", color = TextPrimary, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNavigateToGitHub,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer)
                ) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = EmeraldReady)
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text("GitHub Hub", color = TextPrimary, fontSize = 13.sp)
                }

                Button(
                    onClick = onNavigateToSkillFactory,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricIndigo)
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text("Skill Factory", color = TextPrimary, fontSize = 13.sp)
                }
            }
        }
    }
}
