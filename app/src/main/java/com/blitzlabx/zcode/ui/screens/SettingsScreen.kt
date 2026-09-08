package com.blitzlabx.zcode.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun SettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val theme by viewModel.theme.collectAsState(initial = "system")
    val autoDetect by viewModel.autoDetect.collectAsState(initial = true)
    val preserveExact by viewModel.preserveExact.collectAsState(initial = true)
    val autoCopy by viewModel.autoCopy.collectAsState(initial = false)
    val saveHistory by viewModel.saveHistory.collectAsState(initial = true)
    val confirmDestructive by viewModel.confirmDestructive.collectAsState(initial = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Appearance", color = Color(0xFF8AA0C0))
            listOf("Light" to "light", "Dark" to "dark", "System" to "system").forEach { (label, value) ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(label, color = Color.White)
                    RadioButton(selected = theme == value, onClick = { viewModel.setTheme(value) })
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Behavior", color = Color(0xFF8AA0C0))
            SettingSwitch("Auto-detect configuration", autoDetect) { viewModel.setAutoDetect(it) }
            SettingSwitch("Preserve exact text", preserveExact) { viewModel.setPreserveExact(it) }
            SettingSwitch("Auto-copy result", autoCopy) { viewModel.setAutoCopy(it) }
            SettingSwitch("Save history", saveHistory) { viewModel.setSaveHistory(it) }
            SettingSwitch("Confirm destructive actions", confirmDestructive) { viewModel.setConfirmDestructive(it) }
            Spacer(Modifier.height(16.dp))
            TextButton(onClick = { onNavigate("security") }) { Text("Security Settings →") }
            TextButton(onClick = { onNavigate("import_export") }) { Text("Import / Export →") }
            TextButton(onClick = { onNavigate("documentation") }) { Text("Documentation →") }
            TextButton(onClick = { onNavigate("about") }) { Text("About →") }
        }
    }
}

@Composable
fun SettingSwitch(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.White)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
