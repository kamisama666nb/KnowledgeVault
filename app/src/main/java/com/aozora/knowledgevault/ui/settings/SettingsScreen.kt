package com.aozora.knowledgevault.ui.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    currentApiKey: String = "",
    currentBaseUrl: String = "",
    currentModel: String = "",
    onSaveAIConfig: (apiKey: String, baseUrl: String, model: String) -> Unit
) {
    var apiKey by remember { mutableStateOf(currentApiKey) }
    var baseUrl by remember { mutableStateOf(currentBaseUrl.ifBlank { "https://api.openai.com/v1" }) }
    var model by remember { mutableStateOf(currentModel.ifBlank { "gpt-3.5-turbo" }) }
    
    var showApiKey by remember { mutableStateOf(false) }
    var showSaveSuccess by remember { mutableStateOf(false) }
    
    var expandedSection by remember { mutableStateOf("ai") } // ai, about, advanced
    
    // 保存成功提示
    LaunchedEffect(showSaveSuccess) {
        if (showSaveSuccess) {
            kotlinx.coroutines.delay(2000)
            showSaveSuccess = false
        }
    }
    
    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                tonalElevation = 0.dp
            ) {
                TopAppBar(
                    title = { 
                        Text(
                            "设置",
                            fontWeight = FontWeight.Light,
                            letterSpacing = 1.5.sp
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Outlined.ArrowBack, "返回")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        },
        snackbarHost = {
            // 保存成功提示
            AnimatedVisibility(
                visible = showSaveSuccess,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.CheckCircle, null, modifier = Modifier.size(20.dp))
                        Text("设置已保存", fontWeight = FontWeight.Normal)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // AI配置区
            SettingsSection(
                title = "AI 增强",
                icon = Icons.Outlined.AutoAwesome,
                expanded = expandedSection == "ai",
                onExpandChange = { expandedSection = if (it) "ai" else "" }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // 说明
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Info,
                                null,
                                tint = MaterialTheme.colorS
