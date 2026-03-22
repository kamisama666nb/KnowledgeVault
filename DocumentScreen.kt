package com.aozora.knowledgevault.ui.document

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentScreen(
    uiState: DocumentUiState,
    onBack: () -> Unit,
    onStartEdit: () -> Unit,
    onSaveEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onToggleStar: () -> Unit,
    onRegenerateSummary: () -> Unit,
    onAskQuestion: (String) -> Unit
) {
    var showAskDialog by remember { mutableStateOf(false) }
    var questionText by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (uiState.isEditing) "编辑文档" else "文档详情",
                        maxLines = 1
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                actions = {
                    if (uiState.isEditing) {
                        IconButton(onClick = onCancelEdit) {
                            Icon(Icons.Default.Close, "取消")
                        }
                        IconButton(onClick = onSaveEdit) {
                            Icon(Icons.Default.Check, "保存")
                        }
                    } else {
                        IconButton(onClick = onToggleStar) {
                            Icon(
                                imageVector = if (uiState.document?.isStarred == true) 
                                    Icons.Filled.Star 
                                else 
                                    Icons.Default.StarBorder,
                                contentDescription = "收藏"
                            )
                        }
                        IconButton(onClick = onStartEdit) {
                            Icon(Icons.Default.Edit, "编辑")
                        }
                        IconButton(onClick = { showAskDialog = true }) {
                            Icon(Icons.Default.QuestionAnswer, "向AI提问")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                if (uiState.isEditing) {
                    // 编辑模式
                    OutlinedTextField(
                        value = uiState.editTitle,
                        onValueChange = onTitleChanged,
                        label = { Text("标题") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = uiState.editContent,
                        onValueChange = onContentChanged,
                        label = { Text("内容") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 300.dp),
                        minLines = 10
                    )
                } else {
                    // 查看模式
                    uiState.document?.let { doc ->
                        Text(
                            text = doc.title,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 元信息
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "来源: ${doc.source}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatDate(doc.updatedAt),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 摘要卡片
                        doc.summary?.let { summary ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "AI摘要",
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        TextButton(onClick = onRegenerateSummary) {
                                            Text("重新生成")
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = summary,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        // 标签
                        if (doc.tags.isNotEmpty()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                doc.tags.forEach { tag ->
                                    AssistChip(
                                        onClick = { },
                                        label = { Text(tag) },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Label,
                                                null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        Divider()
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 内容
                        Text(
                            text = doc.content,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        // AI回答
                        uiState.aiAnswer?.let { answer ->
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "AI回答",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = answer,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // 提问对话框
    if (showAskDialog) {
        AlertDialog(
            onDismissRequest = { showAskDialog = false },
            title = { Text("向AI提问") },
            text = {
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("请输入问题") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (questionText.isNotBlank()) {
                            onAskQuestion(questionText)
                            showAskDialog = false
                            questionText = ""
                        }
                    },
                    enabled = !uiState.isAskingAI
                ) {
                    Text("提问")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAskDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
    
    // 错误提示
    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = { },
            title = { Text("错误") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { /* clear error */ }) {
                    Text("确定")
                }
            }
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
