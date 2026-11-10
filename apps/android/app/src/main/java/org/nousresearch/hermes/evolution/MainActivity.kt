package org.nousresearch.hermes.evolution

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import org.nousresearch.hermes.evolution.engine.AgentState
import org.nousresearch.hermes.evolution.engine.HermesBridge
import org.nousresearch.hermes.evolution.engine.HermesEngineService
import org.nousresearch.hermes.evolution.engine.HermesEvent
import org.nousresearch.hermes.evolution.ui.components.ApprovalDialog
import org.nousresearch.hermes.evolution.ui.screens.ActivityLogsScreen
import org.nousresearch.hermes.evolution.ui.screens.AndroidToolsScreen
import org.nousresearch.hermes.evolution.ui.screens.AutomationsScreen
import org.nousresearch.hermes.evolution.ui.screens.ChatScreen
import org.nousresearch.hermes.evolution.ui.screens.DashboardScreen
import org.nousresearch.hermes.evolution.ui.screens.DeveloperWorkspaceScreen
import org.nousresearch.hermes.evolution.ui.screens.EvolutionScreen
import org.nousresearch.hermes.evolution.ui.screens.GitHubHubScreen
import org.nousresearch.hermes.evolution.ui.screens.KnowledgeVaultScreen
import org.nousresearch.hermes.evolution.ui.screens.ModelManagerScreen
import org.nousresearch.hermes.evolution.ui.screens.RemoteWorkersScreen
import org.nousresearch.hermes.evolution.ui.screens.SecurityScreen
import org.nousresearch.hermes.evolution.ui.screens.SettingsScreen
import org.nousresearch.hermes.evolution.ui.screens.SkillFactoryScreen
import org.nousresearch.hermes.evolution.ui.screens.SkillLibraryScreen
import org.nousresearch.hermes.evolution.ui.screens.TaskManagerScreen
import org.nousresearch.hermes.evolution.ui.screens.YouTubeStudioScreen
import org.nousresearch.hermes.evolution.ui.theme.CyanGlow
import org.nousresearch.hermes.evolution.ui.theme.ElectricIndigo
import org.nousresearch.hermes.evolution.ui.theme.HermesEvolutionTheme
import org.nousresearch.hermes.evolution.ui.theme.SurfaceContainer
import org.nousresearch.hermes.evolution.ui.theme.SurfaceDark
import org.nousresearch.hermes.evolution.ui.theme.TextMuted
import org.nousresearch.hermes.evolution.ui.theme.TextPrimary
import org.nousresearch.hermes.evolution.ui.theme.VoidBlack

class MainActivity : ComponentActivity() {

    private lateinit var bridge: HermesBridge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bridge = HermesBridge.getInstance(this)
        HermesEngineService.start(this)

        setContent {
            HermesEvolutionTheme {
                val agentState by bridge.agentState.collectAsState()
                var currentScreen by remember { mutableStateOf("home") }
                var chatInitialPrompt by remember { mutableStateOf<String?>(null) }
                var activeApproval by remember { mutableStateOf<HermesEvent.ToolApprovalRequired?>(null) }

                LaunchedEffect(Unit) {
                    bridge.events.collectLatest { event ->
                        if (event is HermesEvent.ToolApprovalRequired) {
                            activeApproval = event
                        }
                    }
                }

                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = SurfaceDark,
                            contentColor = TextPrimary
                        ) {
                            NavigationBarItem(
                                selected = currentScreen == "home",
                                onClick = { currentScreen = "home" },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ElectricIndigo,
                                    selectedTextColor = ElectricIndigo,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = SurfaceContainer
                                )
                            )
                            NavigationBarItem(
                                selected = currentScreen == "chat",
                                onClick = { currentScreen = "chat" },
                                icon = { Icon(Icons.Default.Chat, contentDescription = "Chat") },
                                label = { Text("Chat", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ElectricIndigo,
                                    selectedTextColor = ElectricIndigo,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = SurfaceContainer
                                )
                            )
                            NavigationBarItem(
                                selected = currentScreen == "models",
                                onClick = { currentScreen = "models" },
                                icon = { Icon(Icons.Default.Memory, contentDescription = "Models") },
                                label = { Text("Models", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ElectricIndigo,
                                    selectedTextColor = ElectricIndigo,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = SurfaceContainer
                                )
                            )
                            NavigationBarItem(
                                selected = currentScreen == "skills",
                                onClick = { currentScreen = "skills" },
                                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Skills") },
                                label = { Text("Skills", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ElectricIndigo,
                                    selectedTextColor = ElectricIndigo,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = SurfaceContainer
                                )
                            )
                            NavigationBarItem(
                                selected = currentScreen in listOf("youtube", "github", "dev", "logs", "security", "settings", "evolution", "knowledge", "tasks", "tools", "automations", "remote"),
                                onClick = { currentScreen = "youtube" },
                                icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "Studios") },
                                label = { Text("Studios", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = CyanGlow,
                                    selectedTextColor = CyanGlow,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = SurfaceContainer
                                )
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(VoidBlack)
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            "home" -> DashboardScreen(
                                agentState = agentState,
                                onNavigateToChat = { prompt ->
                                    chatInitialPrompt = prompt
                                    currentScreen = "chat"
                                },
                                onNavigateToYouTube = { currentScreen = "youtube" },
                                onNavigateToGitHub = { currentScreen = "github" },
                                onNavigateToSkillFactory = { currentScreen = "skill_factory" }
                            )
                            "chat" -> ChatScreen(
                                agentState = agentState,
                                initialPrompt = chatInitialPrompt,
                                onSendMessage = { text ->
                                    bridge.processMessage(text)
                                },
                                onCancelTurn = {
                                    bridge.cancelTurn()
                                }
                            )
                            "models" -> ModelManagerScreen(
                                onSaveKey = { key, value ->
                                    HermesApplication.instance.credentialVault.storeSecret(key, value)
                                },
                                onRunBenchmark = { _ -> }
                            )
                            "skills" -> SkillLibraryScreen(
                                onNavigateToFactory = { currentScreen = "skill_factory" }
                            )
                            "skill_factory" -> SkillFactoryScreen(
                                onSkillCreated = { currentScreen = "skills" }
                            )
                            "youtube" -> YouTubeStudioScreen()
                            "github" -> GitHubHubScreen()
                            "evolution" -> EvolutionScreen()
                            "knowledge" -> KnowledgeVaultScreen()
                            "tasks" -> TaskManagerScreen()
                            "dev" -> DeveloperWorkspaceScreen()
                            "tools" -> AndroidToolsScreen()
                            "automations" -> AutomationsScreen()
                            "remote" -> RemoteWorkersScreen()
                            "logs" -> ActivityLogsScreen()
                            "security" -> SecurityScreen()
                            "settings" -> SettingsScreen()
                        }

                        // Approval Dialog Modal
                        activeApproval?.let { approval ->
                            ApprovalDialog(
                                callId = approval.callId,
                                toolName = approval.toolName,
                                arguments = approval.arguments,
                                riskLevel = approval.riskLevel,
                                onDecision = { approved ->
                                    bridge.submitToolApproval(approval.callId, approved)
                                    activeApproval = null
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
