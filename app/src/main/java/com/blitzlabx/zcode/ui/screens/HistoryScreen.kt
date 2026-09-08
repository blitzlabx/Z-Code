package com.blitzlabx.zcode.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.blitzlabx.zcode.ui.theme.ZBackground
import com.blitzlabx.zcode.ui.theme.ZSurface
import com.blitzlabx.zcode.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val history by viewModel.history.collectAsState()
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZBackground)
            )
        },
        containerColor = ZBackground
    ) { padding ->
        if (history.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No history yet", color = Color(0xFF6A8AAA))
            }
        } else {
            LazyColumn(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(history, key = { it.id }) { item ->
                    Card(colors = CardDefaults.cardColors(containerColor = ZSurface)) {
                        Row(Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    if (item.type == "generate") "Generate" else "Translate",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    if (item.type == "generate") "${item.originalText.take(30)} → ${item.zCode.take(20)}..."
                                    else "${item.zCode.take(20)}... → ${item.originalText.take(30)}",
                                    color = Color(0xFF8AA0C0),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(sdf.format(Date(item.timestamp)), color = Color(0xFF5A6A8A), style = MaterialTheme.typography.labelSmall)
                            }
                            IconButton(onClick = { viewModel.deleteHistoryItem(item.id) }) {
                                Icon(Icons.Default.Delete, null, tint = Color(0xFFFF5252))
                            }
                        }
                    }
                }
            }
        }
    }
}
