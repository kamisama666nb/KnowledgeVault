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
                                tint = MaterialTheme.colorS                                null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                "配置AI服务后可使用自动摘要、智能标签等功能",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                lineHeight = 18.sp
                            )
                        }
                    }
                    
                    // API Key
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = { Text("API Key") },
                        placeholder = { Text("sk-...") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (showApiKey) 
                            VisualTransformation.None 
                        else 
                            PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showApiKey = !showApiKey }) {
                                Icon(
                                    if (showApiKey) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    if (showApiKey) "隐藏" else "显示",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    
                    // Base URL
                    OutlinedTextField(
                        value = baseUrl,
                        onValueChange = { baseUrl = it },
                        label = { Text("API地址") },
                        placeholder = { Text("https://api.openai.com/v1") },
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = { 
                            Text(
                                "可使用OpenAI兼容的API服务",
                                style = MaterialTheme.typography.labelSmall
                            ) 
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    
                    // Model
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text("模型名称") },
                        placeholder = { Text("gpt-3.5-turbo") },
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = { 
                            Text(
                                "如：gpt-3.5-turbo, gpt-4, deepseek-chat",
                                style = MaterialTheme.typography.labelSmall
                            ) 
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    
                    // 保存按钮
                    Button(
                        onClick = {
                            onSaveAIConfig(apiKey, baseUrl, model)
                            showSaveSuccess = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = apiKey.isNotBlank()
                    ) {
                        Icon(Icons.Outlined.Save, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("保存配置", fontSize = 16.sp)
                    }
                    
                    // 快速预设
                    Text(
                        "快速预设",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = {
                                baseUrl = "https://api.openai.com/v1"
                                model = "gpt-3.5-turbo"
                            },
                            label = { Text("OpenAI") },
                            leadingIcon = { Icon(Icons.Outlined.Language, null, modifier = Modifier.size(16.dp)) }
                        )
                        
                        AssistChip(
                            onClick = {
                                baseUrl = "https://api.deepseek.com/v1"
                                model = "deepseek-chat"
                            },
                            label = { Text("Deepseek") },
                            leadingIcon = { Icon(Icons.Outlined.Code, null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }
            
            // 关于区
            SettingsSection(
                title = "关于",
                icon = Icons.Outlined.Info,
                expanded = expandedSection == "about",
                onExpandChange = { expandedSection = if (it) "about" else "" }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    InfoRow("版本", "1.0.0")
                    InfoRow("作者", "Aozora")
                    InfoRow("开源协议", "MIT License")
                }
            }
            
            // 高级设置
            SettingsSection(
                title = "高级",
                icon = Icons.Outlined.Tune,
                expanded = expandedSection == "advanced",
                onExpandChange = { expandedSection = if (it) "advanced" else "" }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = { /* TODO: 清除缓存 */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.CleaningServices, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("清除缓存")
                    }
                    
                    TextButton(
                        onClick = { /* TODO: 导出数据 */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.Download, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("导出数据")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {
            // 标题栏
            Surface(
                onClick = { onExpandChange(!expanded) },
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            icon,
                            null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    
                    Icon(
                        if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                        null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
            // 内容区
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Box(modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal
        )
    }
}
