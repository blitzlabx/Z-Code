package com.blitzlabx.zcode.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.blitzlabx.zcode.ui.theme.ZBackground
import com.blitzlabx.zcode.ui.theme.ZBlue
import com.blitzlabx.zcode.ui.theme.ZSuccess
import com.blitzlabx.zcode.ui.theme.ZSurface
import com.blitzlabx.zcode.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslatedOutputScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNew: () -> Unit
) {
    val result by viewModel.decodeResult.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Translator") },
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
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = ZSuccess.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Decoding Successful", color = ZSuccess, modifier = Modifier.padding(12.dp))
            }
            Spacer(Modifier.height(16.dp))
            result?.let { r ->
                Text("Original Text", color = Color(0xFF8AA0C0))
                Card(
                    colors = CardDefaults.cardColors(containerColor = ZSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(r.text, color = Color.White, modifier = Modifier.padding(16.dp))
                }
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text("Z-Code Length: ${r.zCodeLength}", color = Color(0xFF8AA0C0))
                    Text("Decoded Length: ${r.originalLength}", color = Color(0xFF8AA0C0))
                }
                Spacer(Modifier.height(16.dp))
                Text("Detected Configuration", color = Color(0xFF8AA0C0))
                Card(colors = CardDefaults.cardColors(containerColor = ZSurface), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("• Code Type: ${r.detectedConfig.codeType.displayName}", color = Color.White)
                        Text("• Hash: ${r.detectedConfig.hash.displayName}", color = Color.White)
                        Text("• Z Language: ${r.detectedConfig.zLanguage.displayName}", color = Color.White)
                        Text("• Mode: ${r.detectedConfig.mode.displayName}", color = Color.White)
                        Text("• Verified: ${if (r.verified) "Yes" else "No"}", color = Color.White)
                    }
                }
                Spacer(Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {
                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cm.setPrimaryClip(ClipData.newPlainText("Decoded", r.text))
                    }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.ContentCopy, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Copy")
                    }
                    OutlinedButton(onClick = {
                        val send = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, r.text)
                        }
                        context.startActivity(Intent.createChooser(send, "Share"))
                    }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Share, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Share")
                    }
                }
                Spacer(Modifier.height(12.dp))
                Button(onClick = onNew, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = ZBlue)) {
                    Text("New", color = Color.Black)
                }
            }
        }
    }
}
