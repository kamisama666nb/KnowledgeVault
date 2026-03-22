package com.aozora.knowledgevault.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSaveAIConfig: (String, String, String) -> Unit
) {
    var apiKey by remember { mutableStateOf("") }
    var baseUrl by remember { mutableStateOf("https://api.openai.com/v1") }
    var model by remember { mutableStateOf("gpt-3.5-turbo") }
    var showSaveSuccess by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "AI配置",
                style = MaterialTheme.typography.titleLarge
            )
            
            Text(
                text = "配置你的AI API密钥，用于自动生成摘要、标签和智能问答",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key") },
                placeholder = { Text("sk-...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = baseUrl,
                onValueChange = { baseUrl = it },
                label = { Text("Base URL") },
                placeholder = { Text("https://api.openai.com/v1") },
                modifier = Modifier.fillMaxWidth(),
                supportingText = { 
                    Text("支持OpenAI、Deepseek、Qwen等兼容API") 
                },
                singleLine = true
            )
            
            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("模型") },
                placeholder = { Text("gpt-3.5-turbo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = {
                    onSaveAIConfig(apiKey, baseUrl, model)
                    showSaveSuccess = true
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = apiKey.isNotBlank()
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("保存配置")
            }
            
            Divider()
            
            Text(
                text = "关于",
                style = MaterialTheme.typography.titleLarge
            )
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "KnowledgeVault",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "版本: 1.0.0",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "个人知识库管理助手",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "功能特性",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text("• 文档管理和全文搜索", style = MaterialTheme.typography.bodySmall)
                    Text("• AI自动摘要和标签", style = MaterialTheme.typography.bodySmall)
                    Text("• RSS订阅和内容聚合", style = MaterialTheme.typography.bodySmall)
                    Text("• 智能问答", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
    
    if (showSaveSuccess) {
        AlertDialog(
            onDismissRequest = { showSaveSuccess = false },
            title = { Text("保存成功") },
            text = { Text("AI配置已保存") },
            confirmButton = {
                TextButton(onClick = { showSaveSuccess = false }) {
                    Text("确定")
                }
            }
        )
    }
}
