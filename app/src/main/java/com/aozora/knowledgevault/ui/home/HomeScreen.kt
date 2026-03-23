package com.aozora.knowledgevault.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aozora.knowledgevault.data.database.DocumentEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onDocumentClick: (Long) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onFilterChanged: (String) -> Unit,
    onToggleStar: (Long) -> Unit,
    onDeleteDocument: (Long) -> Unit,
    onAddDocument: () -> Unit,
    onOpenRss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var showFilterMenu by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // 渐变背景
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        )
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                // 毛玻璃顶栏
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    tonalElevation = 0.dp
                ) {
                    Column {
                        // 顶部栏
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 左侧：标题
                            Text(
                                "知识库",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Light,
                                    letterSpacing = 2.sp
                                ),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                            
                            // 右侧：功能按钮
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // 筛选
                                Box {
                                    IconButton(onClick = { showFilterMenu = true }) {
                                        Icon(
                                            Icons.Outlined.FilterList,
                                            "筛选",
                                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                    
                                    DropdownMenu(
                                        expanded = showFilterMenu,
                                        onDismissRequest = { showFilterMenu = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("全部") },
                                            onClick = {
                                                onFilterChanged("all")
                                                showFilterMenu = false
                                            },
                                            leadingIcon = { Icon(Icons.Outlined.Description, null) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("收藏") },
                                            onClick = {
              }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddDocument,
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Outlined.Add, "新建")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 搜索框
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                placeholder = { 
                    Text(
                        "搜索知识...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Light
                        )
                    ) 
                },
                leadingIcon = { 
                    Icon(
                        Icons.Outlined.Search, 
                        null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    ) 
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            )
            
            // 内容区域
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(strokeWidth = 2.dp)
                    }
                }
                
                uiState.documents.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Outlined.MenuBook,
                                null,
                                modifier = Modifier.size(72.dp).alpha(0.2f)
                            )
                            Text(
                                text = if (uiState.searchQuery.isNotBlank()) 
                                    "未找到相关内容" 
                                else 
                                    "开始你的知识旅程",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Light
                                ),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp, 4.dp, 16.dp, 88.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.documents,
                            key = { it.id }
                        ) { document ->
                            DocumentItem(
                                document = document,
                                onClick = { onDocumentClick(document.id) },
                                onToggleStar = { onToggleStar(document.id) },
                                onDelete = { onDeleteDocument(document.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentItem(
    document: DocumentEntity,
    onClick: () -> Unit,
    onToggleStar: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = document.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row {
                    IconButton(onClick = onToggleStar) {
                        Icon(
                            imageVector = if (document.isStarred) 
                                Icons.Filled.Star 
                            else 
                                Icons.Outlined.StarBorder,
                            contentDescription = "收藏",
                            tint = if (document.isStarred) 
                                Color(0xFFFFB74D)
                            else 
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                    
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Outlined.MoreHoriz, "更多")
                        }
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("删除") },
                                onClick = {
                                    onDelete()
                                    showMenu = false
                                }
                            )
                        }
                    }
                }
            }
            
            document.summary?.let { summary ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Light
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            if (document.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    document.tags.take(3).forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDate(document.updatedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                )
                
                Text(
                    text = document.source,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
