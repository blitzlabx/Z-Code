package com.blitzlabx.zcode.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.blitzlabx.zcode.ui.theme.ZBackground
import com.blitzlabx.zcode.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuritySettingsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val requirePwd by viewModel.requirePasswordSecure.collectAsState(initial = true)
    val clearAfter by viewModel.clearPasswordAfter.collectAsState(initial = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security") },
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
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Password Behavior", color = Color(0xFF8AA0C0))
            SettingSwitch("Require password for secure modes", requirePwd) { viewModel.setRequirePasswordSecure(it) }
            SettingSwitch("Clear password after use", clearAfter) { viewModel.setClearPasswordAfter(it) }
            Spacer(Modifier.height(24.dp))
            Text("Local Data", color = Color(0xFF8AA0C0))
            Button(
                onClick = { viewModel.clearHistory() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
            ) {
                Text("Clear All Data")
            }
            Text(
                "This will remove all stored data including history, configurations and cached files.",
                color = Color(0xFFFFAB40),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
