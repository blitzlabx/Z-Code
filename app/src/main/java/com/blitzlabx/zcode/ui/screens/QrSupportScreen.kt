package com.blitzlabx.zcode.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.blitzlabx.zcode.ui.theme.ZBackground
import com.blitzlabx.zcode.ui.theme.ZBlue
import com.blitzlabx.zcode.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrSupportScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val result by viewModel.zCodeResult.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QR Support") },
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
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Generate QR / Scan QR", color = Color(0xFF8AA0C0))
            Spacer(Modifier.height(16.dp))
            // Placeholder for QR – in production use ZXing to render Bitmap
            Card(
                modifier = Modifier.size(220.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        result?.zCode?.take(40) ?: "Generate a Z-Code first",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            result?.let {
                Text(it.zCode.take(60) + if (it.zCode.length > 60) "..." else "", color = Color.White)
            }
            Spacer(Modifier.height(24.dp))
            Button(onClick = { }, colors = ButtonDefaults.buttonColors(containerColor = ZBlue)) {
                Text("Share", color = Color.Black)
            }
        }
    }
}
