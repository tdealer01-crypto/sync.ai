package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.*
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    private val viewModel: AgiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var currentScreen by remember { mutableStateOf("login") }

                Box(modifier = Modifier.fillMaxSize()) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.lisa_background_1782272440866),
                        contentDescription = "Background",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Dimmer to make text readable
                    Box(modifier = Modifier.fillMaxSize().background(Color(0x661A0000)))
                    
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent
                    ) { innerPadding ->
                        Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                            when (screen) {
                                "login" -> {
                                    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                                        LoginScreen(onLoginSuccess = { currentScreen = "dashboard" })
                                    }
                                }
                                "dashboard" -> {
                                    AgiDashboard(
                                        viewModel = viewModel,
                                        modifier = Modifier
                                            .padding(innerPadding)
                                            .fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AgiDashboard(viewModel: AgiViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()
    var inputText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }
        
        // --- HEADER ---
        item { HeaderSection() }

        // --- ACCESS LAYER & INPUT ---
        item {
            ArchBlock(
                title = "1. ACCESS LAYER",
                icon = Icons.Default.Computer,
                color = ArchBlue
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    MiniBadge("Web UI", Icons.Default.Web)
                    MiniBadge("Mobile App", Icons.Default.Smartphone)
                    MiniBadge("API", Icons.Default.Api)
                    MiniBadge("CLI/SDK", Icons.Default.Terminal)
                }
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Enter prompt to execute...", color = ArchTextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ArchBlue,
                        unfocusedBorderColor = ArchBorder,
                        focusedTextColor = ArchTextMain,
                        unfocusedTextColor = ArchTextMain,
                        cursorColor = ArchBlue
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        viewModel.updateTask(inputText)
                        viewModel.startPipeline()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ArchBlue),
                    shape = RoundedCornerShape(8.dp),
                    enabled = state.pipelineStage == PipelineStage.IDLE || state.pipelineStage == PipelineStage.FINISHED || state.pipelineStage == PipelineStage.BLOCKED
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = ArchTextMain)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("TRIGGER REQUEST", fontWeight = FontWeight.Bold, color = ArchTextMain, letterSpacing = 1.sp)
                }
            }
        }

        // --- LEFT COLUMN (DSG, MEMORY, DATA) ---
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    ArchBlock("DSG POLICY ENGINE v2", Icons.Default.Security, ArchPurple, state.activeBlock == "DSG_POLICY" || state.pipelineStage == PipelineStage.BLOCKED) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            CheckItem("Input Validation", true)
                            CheckItem("Policy Check", true)
                            CheckItem("Risk Analysis", true)
                            CheckItem("Compliance Check", true)
                            CheckItem("Cost & Quota Check", true)
                        }
                        if (state.pipelineStage == PipelineStage.BLOCKED) {
                            Text("❌ ACCESS DENIED", color = ArchDanger, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(top = 12.dp))
                        }
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    ArchBlock("MEMORY LAYER", Icons.Default.Storage, ArchBlue) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconListItem("Vector DB", Icons.Default.Map)
                            IconListItem("Knowledge Graph", Icons.Default.Share)
                            IconListItem("Episodic Memory", Icons.Default.History)
                            IconListItem("Long-Term Memory", Icons.Default.Memory)
                        }
                    }
                }
            }
        }

        // --- CORE ORCHESTRATOR ---
        item {
            ArchBlock(
                title = "CORE ORCHESTRATOR",
                icon = Icons.Default.Psychology,
                color = ArchBlue,
                isActive = state.pipelineStage == PipelineStage.ORCHESTRATOR
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    OrchestratorNode("Planner", Icons.Default.Psychology, state.activeBlock == "PLANNER" || state.pipelineStage > PipelineStage.ORCHESTRATOR)
                    HorizontalDivider(modifier = Modifier.weight(1f).align(Alignment.CenterVertically).padding(horizontal = 4.dp), color = ArchBorder)
                    OrchestratorNode("DAG Build", Icons.Default.AccountTree, state.activeBlock == "DAG" || state.pipelineStage > PipelineStage.ORCHESTRATOR)
                    HorizontalDivider(modifier = Modifier.weight(1f).align(Alignment.CenterVertically).padding(horizontal = 4.dp), color = ArchBorder)
                    OrchestratorNode("Scheduler", Icons.Default.Schedule, state.activeBlock == "SCHEDULER" || state.pipelineStage > PipelineStage.ORCHESTRATOR)
                    HorizontalDivider(modifier = Modifier.weight(1f).align(Alignment.CenterVertically).padding(horizontal = 4.dp), color = ArchBorder)
                    OrchestratorNode("Router", Icons.Default.Route, state.activeBlock == "DISPATCHER" || state.activeBlock == "ROUTER" || state.pipelineStage > PipelineStage.ORCHESTRATOR)
                }
            }
        }

        // --- RIGHT COLUMN (MODEL ROUTER & MODELS) VS DATA TOOLS ---
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    ArchBlock("DATA & TOOL INTEGRATION", Icons.Default.Build, ArchYellow) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconListItem("Web Search", Icons.Default.Search)
                            IconListItem("RAG Pipeline", Icons.Default.CompareArrows)
                            IconListItem("API Integrations", Icons.Default.SettingsEthernet)
                            IconListItem("Code Execution", Icons.Default.Code)
                        }
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    ArchBlock("MODEL ROUTER", Icons.Default.Route, ArchCyan, isActive = state.activeBlock == "ROUTER") {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            MiniBadge("Llama 4 Maverick", Icons.Default.AllInclusive, fullWidth = true)
                            MiniBadge("OpenAI GPT-4o", Icons.Default.Chat, fullWidth = true)
                            MiniBadge("Local vLLM", Icons.Default.Dns, fullWidth = true)
                        }
                    }
                }
            }
        }

        // --- EXECUTION ENGINE ---
        item {
            ArchBlock(
                title = "EXECUTION ENGINE (PARALLEL)",
                icon = Icons.Default.Memory,
                color = ArchCyan,
                isActive = state.pipelineStage == PipelineStage.EXECUTION
            ) {
                if (state.taskStore.isEmpty()) {
                    Text("Awaiting dispatch...", color = ArchTextMuted, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.taskStore.values.forEach { worker ->
                            WorkerRow(worker)
                        }
                        
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp).clip(RoundedCornerShape(8.dp)).background(ArchSurfaceHighlight.copy(alpha = 0.5f)).border(1.dp, ArchBorder, RoundedCornerShape(8.dp)).padding(8.dp), contentAlignment = Alignment.Center) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Autorenew, contentDescription = null, tint = ArchYellow, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Self-Healing & Retry Engine", color = ArchYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // --- API INSPECTOR ---
        item {
            ArchBlock(
                title = "LIVE API TASK INSPECTOR",
                icon = Icons.Default.Api,
                color = ArchYellow
            ) {
                var queryId by remember { mutableStateOf("1") }
                val parsedId = queryId.toIntOrNull() ?: -1

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("GET /tasks/{task_id}", color = ArchCyan, fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = queryId,
                            onValueChange = { queryId = it },
                            placeholder = { Text("Task ID", color = ArchTextMuted) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ArchYellow,
                                unfocusedBorderColor = ArchBorder,
                                focusedTextColor = ArchTextMain,
                                unfocusedTextColor = ArchTextMain
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ArchBackground)
                            .border(1.dp, ArchBorder, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = viewModel.getTaskStatusJson(parsedId),
                            color = ArchTextMain,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // --- CRITIC & EVAL ---
        item {
            ArchBlock(
                title = "CRITIC & EVALUATION ENGINE",
                icon = Icons.Default.Star,
                color = ArchPurple,
                isActive = state.activeBlock == "CRITIC"
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    CriticNode("Quality Scorer", Icons.Default.StarRate)
                    CriticNode("Hallucination", Icons.Default.FindInPage)
                    CriticNode("Safety Check", Icons.Default.Security)
                    CriticNode("Feedback Loop", Icons.Default.Loop)
                }
            }
        }

        // --- BOTTOM STACKS (OBSERVABILITY, SEC, COMPOSER, STORAGE) ---
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    ArchBlock("RESPONSE COMPOSER & STREAMING", Icons.Default.DynamicFeed, ArchEmerald, isActive = state.activeBlock == "COMPOSER" || state.pipelineStage == PipelineStage.FINISHED) {
                        Text("Aggregation -> Ranking -> Summary -> Formatting -> Final Output", color = ArchTextSecondary, fontSize = 9.sp, lineHeight = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.GraphicEq, contentDescription = null, tint = ArchEmerald, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Real-Time Output Stream", color = ArchEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ArchBlock("SECURITY & COMPLIANCE", Icons.Default.Lock, ArchEmerald) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                IconListItem("Encryption In-Transit", Icons.Default.VpnKey)
                                IconListItem("RBAC & Auth", Icons.Default.Group)
                                IconListItem("Audit & Privacy", Icons.Default.PrivacyTip)
                            }
                        }
                        ArchBlock("DATA STORAGE", Icons.Default.Storage, ArchYellow) {
                            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                                ActionIcon(Icons.Default.CloudSync, "Supabase API")
                                ActionIcon(Icons.Default.Storage, "PostgreSQL")
                            }
                        }
                    }
                }
            }
        }

        // --- INFRASTRUCTURE LAYER ---
        item {
            ArchBlock("INFRASTRUCTURE LAYER (Vercel Prod: Ready)", Icons.Default.Cloud, ArchEmerald) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        InfraNode("Next.js Edge", Icons.Default.ViewQuilt)
                        InfraNode("Prod Ready", Icons.Default.CheckCircle)
                        InfraNode("Supabase DB", Icons.Default.Storage)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Vercel Health: ${state.backendHealth}",
                        color = ArchEmerald,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ArchSurfaceHighlight.copy(alpha = 0.5f))
                            .border(1.dp, ArchBorder, RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    )
                }
            }
        }

        // --- TERMINAL SANDBOX ---
        item {
            var terminalInput by remember { mutableStateOf("") }
            val listState = rememberLazyListState()
            
            LaunchedEffect(state.terminalLogs.size) {
                if (state.terminalLogs.isNotEmpty()) {
                    listState.animateScrollToItem(state.terminalLogs.size - 1)
                }
            }

            ArchBlock("SANDBOX TERMINAL", Icons.Default.Terminal, ArchPurple) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ArchBackground)
                            .border(1.dp, ArchBorder, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
                            items(state.terminalLogs) { log ->
                                Text(
                                    text = log,
                                    color = if (log.startsWith(">")) ArchYellow else ArchEmerald,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = terminalInput,
                        onValueChange = { terminalInput = it },
                        placeholder = { Text("sandbox$ ", color = ArchTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ArchPurple,
                            unfocusedBorderColor = ArchBorder,
                            focusedTextColor = ArchTextMain,
                            unfocusedTextColor = ArchTextMain,
                            cursorColor = ArchPurple
                        ),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Send),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                            onSend = {
                                viewModel.executeTerminalCommand(terminalInput)
                                terminalInput = ""
                            }
                        ),
                        trailingIcon = {
                            IconButton(onClick = {
                                viewModel.executeTerminalCommand(terminalInput)
                                terminalInput = ""
                            }) {
                                Icon(Icons.Default.Send, contentDescription = "Send Command", tint = ArchPurple)
                            }
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }

        // --- VIRTUALIZATION & APP CLONER ---
        item {
            ArchBlock("APP CLONER ENGINE", Icons.Default.Smartphone, ArchBlue) {
                Column {
                    Text(
                        "Hardware Virtualization Sandbox",
                        color = ArchTextSecondary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ActionIcon(Icons.Default.ChatBubble, "LINE (Clone)")
                        ActionIcon(Icons.Default.Share, "Social (Clone)")
                        ActionIcon(Icons.Default.VideogameAsset, "Games (Sandbox)")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.executeTerminalCommand("virtualization --clone-app com.linecorp.b612") },
                        colors = ButtonDefaults.buttonColors(containerColor = ArchBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create New App Clone", fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // --- METRICS & LOGS ---
        item {
            ArchBlock("OBSERVABILITY STACK", Icons.Default.Analytics, ArchCyan, isActive = state.pipelineStage == PipelineStage.FINISHED) {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    MetricVal("Latency", "${state.latency}ms")
                    MetricVal("Quality", String.format("%.2f", state.qualityScore))
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ArchBackground)
                        .border(1.dp, ArchBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.logs) { log ->
                            val color = when {
                                log.contains("DENY") || log.contains("Failed") || log.contains("🛑") -> ArchDanger
                                log.contains("ALLOW") || log.contains("Success") || log.contains("✅") -> ArchEmerald
                                log.startsWith("[SUPABASE]") -> ArchEmerald
                                log.startsWith("[DSG") -> ArchEmerald
                                log.startsWith("[ORCH") -> ArchBlue
                                log.startsWith("[EXEC") -> ArchCyan
                                log.startsWith("[MODEL") -> ArchYellow
                                log.startsWith("[CRITIC") -> ArchPurple
                                else -> ArchTextSecondary
                            }
                            Text(
                                text = log,
                                color = color,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun MiniBadge(label: String, icon: ImageVector, fullWidth: Boolean = false) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(ArchSurfaceHighlight)
            .border(1.dp, ArchBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = ArchTextSecondary, modifier = Modifier.size(12.dp))
        Spacer(Modifier.width(4.dp))
        Text(label, color = ArchTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun CheckItem(label: String, passed: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(if (passed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked, contentDescription = null, tint = if (passed) ArchEmerald else ArchTextMuted, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, color = ArchTextSecondary, fontSize = 10.sp)
    }
}

@Composable
fun IconListItem(label: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = ArchBlue, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, color = ArchTextSecondary, fontSize = 10.sp)
    }
}

@Composable
fun HeaderSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("LEVEL ULTRA", color = ArchTextMuted, fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Black)
        Text(
            "AGI-GRADE AGENT ARCHITECTURE", 
            color = ArchTextMain, 
            fontSize = 18.sp, 
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp
        )
        Text("Autonomous • Scalable • Secure • Deterministic", color = ArchBlue, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ArchBlock(
    title: String,
    icon: ImageVector,
    color: Color,
    isActive: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ArchSurface)
            .border(
                width = if (isActive) 1.5.dp else 1.dp,
                color = if (isActive) color.copy(alpha = glowAlpha) else ArchBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, color = ArchTextMain, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            if (isActive) {
                Spacer(Modifier.weight(1f))
                Box(modifier = Modifier.size(6.dp).clip(RoundedCornerShape(3.dp)).background(color.copy(alpha = glowAlpha)))
            }
        }
        content()
    }
}

@Composable
fun PolicyItem(label: String, icon: ImageVector, passed: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = if (passed) ArchEmerald else ArchTextMuted, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, color = ArchTextSecondary, fontSize = 10.sp)
    }
}

@Composable
fun OrchestratorNode(label: String, icon: ImageVector, active: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (active) ArchBlue.copy(alpha = 0.2f) else ArchSurfaceHighlight)
            .border(1.dp, if (active) ArchBlue else ArchBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .width(55.dp)
    ) {
        Icon(icon, contentDescription = null, tint = if (active) ArchBlue else ArchTextMuted, modifier = Modifier.size(16.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, color = if (active) ArchTextMain else ArchTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun CriticNode(label: String, icon: ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(ArchSurfaceHighlight)
            .border(1.dp, ArchBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .width(65.dp)
    ) {
        Icon(icon, contentDescription = null, tint = ArchPurple, modifier = Modifier.size(16.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, color = ArchTextMain, fontSize = 9.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun InfraNode(label: String, icon: ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = null, tint = ArchBlue, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, color = ArchTextSecondary, fontSize = 9.sp)
    }
}

@Composable
fun ActionIcon(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = ArchYellow, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, color = ArchTextSecondary, fontSize = 9.sp)
    }
}

@Composable
fun MetricVal(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = ArchTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
        Text(value, color = ArchTextMain, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun WorkerRow(worker: WorkerState) {
    val infiniteTransition = rememberInfiniteTransition()
    val progressColor = when (worker.status) {
        TaskStatus.COMPLETED -> ArchEmerald
        TaskStatus.FAILED -> ArchDanger
        TaskStatus.RETRYING -> ArchYellow
        else -> ArchCyan
    }
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(600), RepeatMode.Reverse
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ArchSurfaceHighlight)
            .border(1.dp, if (worker.status == TaskStatus.EXECUTING || worker.status == TaskStatus.RETRYING) progressColor.copy(alpha = pulse) else ArchBorder, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(ArchSurface)
                .border(1.dp, ArchBorder, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PrecisionManufacturing, contentDescription = null, tint = progressColor, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(worker.name, color = ArchTextMain, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(worker.model, color = ArchTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { worker.progress },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = progressColor,
                trackColor = ArchBackground
            )
            Spacer(Modifier.height(4.dp))
            Text(worker.status.name, color = progressColor, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
    }
}
