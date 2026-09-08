package com.blitzlabx.zcode.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.blitzlabx.zcode.core.Mode
import com.blitzlabx.zcode.ui.theme.ZBackground
import com.blitzlabx.zcode.ui.theme.ZBlue
import com.blitzlabx.zcode.ui.theme.ZSurface
import com.blitzlabx.zcode.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModesScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val selected by viewModel.mode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modes") },
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
        LazyColumn(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(Mode.entries) { m ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (m == selected) ZBlue.copy(alpha = 0.2f) else ZSurface
                    ),
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.setMode(m) }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(m.displayName, color = Color.White, style = MaterialTheme.typography.titleMedium)
                        Text(m.description, color = Color(0xFF8AA0C0), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
