package com.blitzlabx.zcode.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.blitzlabx.zcode.core.*
import com.blitzlabx.zcode.ui.theme.ZBackground
import com.blitzlabx.zcode.ui.theme.ZBlue
import com.blitzlabx.zcode.ui.theme.ZSurface
import com.blitzlabx.zcode.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onResult: () -> Unit
) {
    val input by viewModel.inputText.collectAsState()
    val codeType by viewModel.codeType.collectAsState()
    val hash by viewModel.hash.collectAsState()
    val zLang by viewModel.zLanguage.collectAsState()
    val mode by viewModel.mode.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val result by viewModel.zCodeResult.collectAsState()
    var showPassword by remember { mutableStateOf(false) }

    LaunchedEffect(result) {
        if (result != null) onResult()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Generator") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZBackground)
            )
        },
        containerColor = ZBackground
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { viewModel.setInputText(it) },
                label = { Text("Enter your message...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ZBlue,
                    unfocusedBorderColor = Color(0xFF3A4A6A)
                ),
                supportingText = { Text("${input.length}/10000") }
            )
            Spacer(Modifier.height(16.dp))
            Text("Configuration", color = Color(0xFF8AA0C0))
            Spacer(Modifier.height(8.dp))

            ConfigDropdown("Code Type", CodeType.entries.map { it.displayName to it }, codeType.displayName) {
                viewModel.setCodeType(it)
            }
            ConfigDropdown("Hash", HashAlgorithm.entries.map { it.displayName to it }, hash.displayName) {
                viewModel.setHash(it)
            }
            ConfigDropdown("Z Language", ZLanguage.entries.map { it.displayName to it }, zLang.displayName) {
                viewModel.setZLanguage(it)
            }
            ConfigDropdown("Mode", Mode.entries.map { it.displayName to it }, mode.displayName) {
                viewModel.setMode(it)
            }

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.setPassword(it) },
                label = { Text("Password (optional)") },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ZBlue)
            )

            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        viewModel.setInputText("")
                        viewModel.setPassword("")
                        viewModel.clearResults()
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Clear") }
                Button(
                    onClick = { viewModel.encode() },
                    enabled = input.isNotBlank() && !isLoading,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ZBlue)
                ) {
                    if (isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                    else Text("Generate", color = Color.Black)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ConfigDropdown(
    label: String,
    options: List<Pair<String, T>>,
    selected: String,
    onSelect: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ZBlue)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (name, value) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onSelect(value)
                        expanded = false
                    }
                )
            }
        }
    }
    Spacer(Modifier.height(8.dp))
}
