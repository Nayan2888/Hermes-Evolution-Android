package org.nousresearch.hermes.evolution

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.nousresearch.hermes.evolution.engine.HermesEngineService
import org.nousresearch.hermes.evolution.ui.theme.HermesEvolutionTheme

class MainActivity : ComponentActivity() {

    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HermesEvolutionTheme {
                HermesCompanionApp(
                    onRegisterFileChooser = { callback ->
                        filePathCallback = callback
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        filePathCallback?.onReceiveValue(null)
        filePathCallback = null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HermesCompanionApp(
    onRegisterFileChooser: (ValueCallback<Array<Uri>>?) -> Unit
) {
    val context = LocalContext.current
    val app = HermesApplication.instance

    var targetUrl by remember { mutableStateOf(app.gatewayUrl) }
    var isOffline by remember { mutableStateOf(false) }
    var pageTitle by remember { mutableStateOf("Hermes Agent") }
    var loadProgress by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var wakeLockActive by remember { mutableStateOf(HermesEngineService.isRunning) }

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    // File picker launcher for WebChromeClient.onShowFileChooser
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val uris: Array<Uri>? = if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data?.clipData != null) {
                val count = data.clipData!!.itemCount
                Array(count) { i -> data.clipData!!.getItemAt(i).uri }
            } else if (data?.data != null) {
                arrayOf(data.data!!)
            } else {
                null
            }
        } else {
            null
        }
        onRegisterFileChooser(null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isOffline) Color(0xFFEF4444) else Color(0xFF10B981),
                            modifier = Modifier.size(10.dp)
                        ) {}
                        Column {
                            Text(
                                text = "Hermes Agent ☤",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                text = if (isOffline) "Gateway Offline" else targetUrl,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isOffline = false
                        webViewInstance?.reload()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reload")
                    }
                    IconButton(onClick = { showSettingsSheet = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Main Web View
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            allowFileAccess = true
                            allowContentAccess = true
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = false
                            cacheMode = WebSettings.LOAD_DEFAULT
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            userAgentString = userAgentString + " HermesAgentAndroid/1.1"
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                isLoading = true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                                isOffline = false
                                view?.title?.let { if (it.isNotBlank()) pageTitle = it }
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                super.onReceivedError(view, request, error)
                                if (request?.isForMainFrame == true) {
                                    isOffline = true
                                    isLoading = false
                                }
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                loadProgress = newProgress
                                if (newProgress >= 100) isLoading = false
                            }

                            override fun onShowFileChooser(
                                webView: WebView?,
                                filePathCallback: ValueCallback<Array<Uri>>?,
                                fileChooserParams: FileChooserParams?
                            ): Boolean {
                                onRegisterFileChooser(filePathCallback)
                                val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                                    type = "*/*"
                                    addCategory(Intent.CATEGORY_OPENABLE)
                                }
                                try {
                                    filePickerLauncher.launch(intent)
                                } catch (e: Exception) {
                                    filePathCallback?.onReceiveValue(null)
                                    return false
                                }
                                return true
                            }
                        }

                        loadUrl(targetUrl)
                        webViewInstance = this
                    }
                },
                update = { view ->
                    if (view.url != targetUrl && !isOffline) {
                        view.loadUrl(targetUrl)
                    }
                }
            )

            // Linear Progress Indicator during navigation
            if (isLoading && loadProgress < 100) {
                LinearProgressIndicator(
                    progress = { loadProgress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Offline Gateway Guidance Overlay
            if (isOffline) {
                OfflineGatewayCard(
                    targetUrl = targetUrl,
                    onRetry = {
                        isOffline = false
                        webViewInstance?.loadUrl(targetUrl)
                    },
                    onOpenTermux = {
                        val termuxIntent = context.packageManager.getLaunchIntentForPackage("com.termux")
                        if (termuxIntent != null) {
                            context.startActivity(termuxIntent)
                        } else {
                            Toast.makeText(context, "Termux app not found on device", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onOpenSettings = { showSettingsSheet = true }
                )
            }
        }
    }

    // Settings Modal Bottom Sheet
    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            SettingsSheetContent(
                currentUrl = targetUrl,
                isWakeLockActive = wakeLockActive,
                onSave = { newUrl, enableWakeLock ->
                    targetUrl = newUrl
                    app.gatewayUrl = newUrl
                    app.isWakeLockEnabled = enableWakeLock
                    wakeLockActive = enableWakeLock

                    if (enableWakeLock) {
                        HermesEngineService.start(context)
                    } else {
                        HermesEngineService.stop(context)
                    }

                    showSettingsSheet = false
                    isOffline = false
                    webViewInstance?.loadUrl(newUrl)
                },
                onDismiss = { showSettingsSheet = false }
            )
        }
    }
}

@Composable
fun OfflineGatewayCard(
    targetUrl: String,
    onRetry: () -> Unit,
    onOpenTermux: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val startCommand = "proot-distro login debian -- python3 /data/data/com.termux/files/home/.gemini/antigravity-cli/scratch/hermes-evolution-android/hermes_cli/main.py dashboard --port 8080 --no-open"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xE60F172A))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.CloudOff,
                            contentDescription = "Offline",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Text(
                    text = "Hermes Gateway Offline",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Localhost par genuine Hermes Agent engine connect nahi hua ($targetUrl).\n\nTermux me official engine start karein:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "hermes gateway run",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Hermes Start Command", "hermes gateway run")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Command copied!", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenTermux,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Open Termux")
                    }

                    Button(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Retry")
                    }
                }

                OutlinedButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Configure URL / Port")
                }
            }
        }
    }
}

@Composable
fun SettingsSheetContent(
    currentUrl: String,
    isWakeLockActive: Boolean,
    onSave: (String, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var urlText by remember { mutableStateOf(currentUrl) }
    var wakeLockState by remember { mutableStateOf(isWakeLockActive) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Hermes Agent Connection Settings",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        OutlinedTextField(
            value = urlText,
            onValueChange = { urlText = it },
            label = { Text("Gateway URL") },
            placeholder = { Text("http://127.0.0.1:8080/") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Background Wake-Lock",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "Keeps CPU awake so scheduled automations and agent turns do not sleep",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = wakeLockState,
                onCheckedChange = { wakeLockState = it }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { urlText = HermesApplication.DEFAULT_GATEWAY_URL },
                modifier = Modifier.weight(1f)
            ) {
                Text("Default (8080)")
            }

            Button(
                onClick = { onSave(urlText.trim(), wakeLockState) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save & Connect")
            }
        }
    }
}
