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
import androidx.compose.ui.draw.scale
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
    onFilterChanged: (FilterMode) -> Unit,
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
                                                onFilterChanged(FilterMode.ALL)
                                                showFilterMenu = false
                                            },
                                            leadingIcon = { Icon(Icons.Outlined.Description, null) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("收藏") },
       onClick = {
                                                onFilterChanged(FilterMode.STARRED)
                                                showFilterMenu = false
                                            },
                                            leadingIcon = { Icon(Icons.Outlined.Star, null) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("归档") },
                                            onClick = {
                                                onFilterChanged(FilterMode.ARCHIVED)
                                                showFilterMenu = false
                                            },
                                            leadingIcon = { Icon(Icons.Outlined.Archive, null) }
                                        )
                                    }
                                }
                                
                                // RSS
                                IconButton(onClick = onOpenRss) {
                                    Icon(
                                        Icons.Outlined.RssFeed,
                                        "RSS",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                                
                                // 设置
                                IconButton(onClick = onOpenSettings) {
                                    Icon(
                                        Icons.Outlined.Settings,
                                        "设置",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                        
                        // 搜索框
                        AnimatedVisibility(
                            visible = true,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            OutlinedTextField(
                                value = uiState.searchQuery,
                                onValueChange = onSearchQueryChanged,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                placeholder = { 
                                    Text(
                                        "搜索知识...",
                                        fontWeight = FontWeight.Light
                                    ) 
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Search,
                                        null,
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    )
                                },
                                trailingIcon = {
                                    if (uiState.searchQuery.isNotBlank()) {
                                        IconButton(onClick = { onSearchQueryChanged("") }) {
                                            Icon(
                                                Icons.Outlined.Close,
                                                "清除",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                }
            },

) {
                        DropdownMenuItem(
                            text = { Text(if (document.isStarred) "取消收藏" else "收藏") },
                            onClick = {
                                onToggleStar()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    if (document.isStarred) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    null
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("删除", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                onDelete()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Delete,
                                    null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
}

// 摘要
            if (!document.summary.isNullOrBlank()) {
                Text(
                    document.summary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            } else if (document.content.isNotBlank()) {
                Text(
                    document.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // 底部：标签和信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 标签
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    document.tags.take(3).forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                        ) {
                            Text(
                                tag,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
                
                // 收藏星标
                if (document.isStarred) {
                    Icon(
                        Icons.Filled.Star,
                        "已收藏",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}
