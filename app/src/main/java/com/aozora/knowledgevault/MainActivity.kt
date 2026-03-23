package com.aozora.knowledgevault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import com.aozora.knowledgevault.data.ai.AIConfig
import com.aozora.knowledgevault.data.ai.OpenAICompatibleProvider
import com.aozora.knowledgevault.data.database.AppDatabase
import com.aozora.knowledgevault.data.repository.DocumentRepository
import com.aozora.knowledgevault.data.repository.RssFeedRepository
import com.aozora.knowledgevault.data.rss.RssService
import com.aozora.knowledgevault.ui.document.DocumentScreen
import com.aozora.knowledgevault.ui.document.DocumentViewModel
import com.aozora.knowledgevault.ui.home.HomeScreen
import com.aozora.knowledgevault.ui.home.HomeViewModel
import com.aozora.knowledgevault.ui.rss.RssScreen
import com.aozora.knowledgevault.ui.rss.RssViewModel
import com.aozora.knowledgevault.ui.settings.SettingsScreen
import com.aozora.knowledgevault.ui.theme.KnowledgeVaultTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    private lateinit var database: AppDatabase
    private var aiProvider: OpenAICompatibleProvider? = null
    private lateinit var documentRepository: DocumentRepository
    private lateinit var rssFeedRepository: RssFeedRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化数据库
        database = AppDatabase.getDatabase(applicationContext)
        
        // 初始化Repositories
        val rssService = RssService()
        documentRepository = DocumentRepository(database.documentDao(), aiProvider)
        rssFeedRepository = RssFeedRepository(
            database.rssFeedDao(),
            rssService,
            documentRepository,
            aiProvider
        )
        
        setContent {
            KnowledgeVaultTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
    
    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()
        val scope = rememberCoroutineScope()
        
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            // 主页
            composable("home") {
                val viewModel = remember {
                    HomeViewModel(documentRepository)
                }
                val uiState by viewModel.uiState.collectAsState()
                
                HomeScreen(
                    uiState = uiState,
                    onDocumentClick = { docId ->
                        navController.navigate("document/$docId")
                    },
                    onSearchQueryChanged = viewModel::onSearchQueryChanged,
                    onFilterChanged = viewModel::onFilterChanged,
                    onToggleStar = viewModel::toggleStar,
                    onDeleteDocument = viewModel::deleteDocument,
                    onAddDocument = {
                        navController.navigate("addDocument")
                    },
                    onOpenRss = {
                        navController.navigate("rss")
                    },
                    onOpenSettings = {
                        navController.navigate("settings")
                    }
                )
            }
            
            // 文档详情
            composable(
                route = "document/{documentId}",
                arguments = listOf(
                    navArgument("documentId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val documentId = backStackEntry.arguments?.getLong("documentId") ?: 0L
                val viewModel = remember(documentId) {
                    DocumentViewModel(documentRepository, documentId)
                }
                val uiState by viewModel.uiState.collectAsState()
                DocumentScreen(
                    uiState = uiState,
                    onBack = { navController.popBackStack() },
                    onStartEdit = viewModel::startEditing,
                    onSaveEdit = viewModel::saveDocument,
                    onCancelEdit = viewModel::cancelEditing,
                    onTitleChanged = viewModel::updateEditTitle,
                    onContentChanged = viewModel::updateEditContent,
                    onToggleStar = viewModel::toggleStar,
                    onRegenerateSummary = viewModel::regenerateSummary,
                    onAskQuestion = viewModel::askQuestion
                )
            }
            
            // RSS管理
            composable("rss") {
                val viewModel = remember {
                    RssViewModel(rssFeedRepository)
                }
                val uiState by viewModel.uiState.collectAsState()
                
                RssScreen(
                    uiState = uiState,
                    onBack = { navController.popBackStack() },
                    onAddFeed = viewModel::addFeed,
                    onDeleteFeed = viewModel::deleteFeed,
                    onToggleFeedActive = viewModel::toggleFeedActive,
                    onFetchFeed = viewModel::fetchFeed,
                    onFetchAllFeeds = viewModel::fetchAllFeeds
                )
            }
            
            // 设置
composable("settings") {
    SettingsScreen(
        onBack = { navController.popBackStack() },
        currentApiKey = aiProvider?.let { /* TODO: 从provider获取 */ } ?: "",
        currentBaseUrl = aiProvider?.let { /* TODO: 从provider获取 */ } ?: "",
        currentModel = aiProvider?.let { /* TODO: 从provider获取 */ } ?: "",
        onSaveAIConfig = { apiKey, baseUrl, model ->
            val config = AIConfig(
                apiKey = apiKey,
                baseUrl = baseUrl,
                model = model
            )
            aiProvider = OpenAICompatibleProvider(config)
            
            // 重新初始化repositories
            documentRepository = DocumentRepository(database.documentDao(), aiProvider)
            rssFeedRepository = RssFeedRepository(
                database.rssFeedDao(),
                RssService(),
                documentRepository,
                aiProvider
            )
        }
    )
}
            
            // 添加文档
            composable("addDocument") {
                AddDocumentScreen(
                    onBack = { navController.popBackStack() },
                    onSave = { title, content ->
                        scope.launch {
                            documentRepository.addDocument(
                                title = title,
                                content = content,
                                source = "manual"
                            )
                            navController.popBackStack()
                        }
                    }
                )
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddDocumentScreen(
        onBack: () -> Unit,
        onSave: (String, String) -> Unit
    ) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("添加文档") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "返回")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { 
                                if (title.isNotBlank() && content.isNotBlank()) {
                                    onSave(title, content)
                                }
                            },
                            enabled = title.isNotBlank() && content.isNotBlank()
                        ) {
                            Icon(Icons.Default.Check, "保存")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("标题") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("内容") },
                    modifier = Modifier
                    .fillMaxWidth()
                        .weight(1f),
                    minLines = 10
                )
            }
        }
    }
}
                
