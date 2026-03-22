package com.aozora.knowledgevault.ui.rss

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aozora.knowledgevault.data.database.RssFeedEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RssScreen(
    uiState: RssUiState,
    onBack: () -> Unit,
    onAddFeed: (String, String) -> Unit,
    onDeleteFeed: (Long) -> Unit,
    onToggleFeedActive: (RssFeedEntity) -> Unit,
    onFetchFeed: (Long) -> Unit,
    onFetchAllFeeds: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var feedUrl by remember { mutableStateOf("") }
    var feedCategory by remember { mutableStateOf("未分类") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RSS订阅") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onFetchAllFeeds,
                        enabled = !uiState.isFetching
                    ) {
                        Icon(Icons.Default.Refresh, "刷新全部")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, "添加订阅")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.feeds.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.RssFeed,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "暂无RSS订阅\n点击右下角 + 添加",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = uiState.feeds,
                        key = { it.id }
                    ) { feed ->
                        RssFeedItem(
                            feed = feed,
                            onToggleActive = { onToggleFeedActive(feed) },
                            onFetch = { onFetchFeed(feed.id) },
                            onDelete = { onDeleteFeed(feed.id) },
                            isFetching = uiState.isFetching
                        )
                    }
                }
            }
            
            // 加载进度提示
            if (uiState.isFetching) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = uiState.fetchProgress ?: "处理中...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
    
    // 添加订阅对话框
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("添加RSS订阅") },
            text = {
                Column {
                    OutlinedTextField(
                        value = feedUrl,
                        onValueChange = { feedUrl = it },
                        label = { Text("RSS地址") },
                        placeholder = { Text("https://example.com/feed.xml") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = feedCategory,
                        onValueChange = { feedCategory = it },
                        label = { Text("分类") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (feedUrl.isNotBlank()) {
                            onAddFeed(feedUrl, feedCategory)
                            showAddDialog = false
                            feedUrl = ""
                            feedCategory = "未分类"
                        }
                    }
                ) {
                    Text("添加")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
    
    // 成功/错误提示
    LaunchedEffect(uiState.successMessage, uiState.error) {
        // 这里应该用SnackBar，简化起见省略
    }
}

@Composable
fun RssFeedItem(
    feed: RssFeedEntity,
    onToggleActive: () -> Unit,
    onFetch: () -> Unit,
    onDelete: () -> Unit,
    isFetching: Boolean
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = feed.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = feed.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row {
                    Switch(
                        checked = feed.isActive,
                        onCheckedChange = { onToggleActive() }
                    )
                    
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, "更多")
                        }
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("立即抓取") },
                                onClick = {
                                    onFetch()
                                    showMenu = false
                                },
                                enabled = !isFetching
                            )
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = feed.url,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = feed.lastFetchedAt?.let { 
                        "最后抓取: ${formatDate(it)}" 
                    } ?: "尚未抓取",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                feed.lastError?.let { error ->
                    Text(
                        text = "错误: $error",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
