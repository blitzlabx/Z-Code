package com.blitzlabx.zcode.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.blitzlabx.zcode.ui.theme.ZBackground
import com.blitzlabx.zcode.ui.theme.ZBlue
import com.blitzlabx.zcode.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportExportScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Import / Export") },
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
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Export as", color = Color(0xFF8AA0C0))
            Button(onClick = { /* share history as TXT via Intent in real use */ }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = ZBlue)) {
                Text("TXT – Plain text file", color = Color.Black)
            }
            Button(onClick = { }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = ZBlue)) {
                Text("JSON – Structured data", color = Color.Black)
            }
            Button(onClick = { }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = ZBlue)) {
                Text("QR Code – Scan to share", color = Color.Black)
            }
            Spacer(Modifier.height(16.dp))
            Text("Import Z-Code", color = Color(0xFF8AA0C0))
            OutlinedButton(onClick = { }, modifier = Modifier.fillMaxWidth()) {
                Text("Choose file or paste")
            }
        }
    }
}
