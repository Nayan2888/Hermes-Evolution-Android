package org.nousresearch.hermes.evolution.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.nousresearch.hermes.evolution.data.model.ChatMessage
import org.nousresearch.hermes.evolution.engine.AgentState
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
fun ChatScreen(
    agentState: AgentState,
    initialPrompt: String? = null,
    onSendMessage: (String) -> Unit,
    onCancelTurn: () -> Unit
) {
    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                sender = "system",
                text = "Hermes Evolution Agent initialized. Ready to execute code, research, skills, and autonomous workflows."
            )
        )
    }

    var inputText by remember { mutableStateOf(initialPrompt ?: "") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
    ) {
        // Active Model & Status Header
        Surface(
            color = SurfaceDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hermes Agent Session",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Engine: CPython 3.14 · Router: Auto",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                if (agentState == AgentState.THINKING || agentState == AgentState.WORKING) {
                    Button(
                        onClick = onCancelTurn,
                        colors = ButtonDefaults.buttonColors(containerColor = RoseError),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = "Cancel", modifier = Modifier.height(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Stop", fontSize = 12.sp, color = TextPrimary)
                    }
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "user"
                val isSystem = msg.sender == "system"

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Surface(
                        color = when {
                            isUser -> ElectricIndigo
                            isSystem -> SurfaceContainer
                            else -> SurfaceDark
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(if (isSystem) 1f else 0.85f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (!isUser) {
                                Text(
                                    text = if (isSystem) "SYSTEM" else "HERMES AGENT",
                                    color = if (isSystem) TextSecondary else CyanGlow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            if (msg.thinking != null) {
                                Surface(
                                    color = SurfaceContainer,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = msg.thinking,
                                        modifier = Modifier.padding(8.dp),
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            Text(
                                text = msg.text,
                                color = TextPrimary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Bottom Input Row
        Surface(
            color = SurfaceDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Message Hermes Agent...", color = TextMuted, fontSize = 14.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricIndigo,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedContainerColor = SurfaceContainer,
                        unfocusedContainerColor = SurfaceContainer,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val userMsg = inputText.trim()
                            messages.add(ChatMessage(sender = "user", text = userMsg))
                            onSendMessage(userMsg)
                            inputText = ""
                        }
                    },
                    modifier = Modifier.background(ElectricIndigo, RoundedCornerShape(20.dp))
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = TextPrimary)
                }
            }
        }
    }
}
