package com.blitzlabx.zcode.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.blitzlabx.zcode.ui.theme.ZBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentationScreen(onBack: () -> Unit) {
    val sections = listOf(
        "What is Z-Code?" to "Z-Code is a compact offline communication encoding system. Turn messages into short, obscure strings and decode them anytime — completely offline.",
        "How it works" to "UTF-8 → optional compression → optional AES-256-GCM encryption (password) → integrity hash → Base-N encoding with selected alphabet.",
        "Code Types" to "Compact, Standard, Extended — affect output density and hash length.",
        "Hashes" to "SHA-256 / SHA-512 provide integrity verification. Hashing is not encryption.",
        "Z Languages" to "Z-Alpha, Z-Numeric, Z-Symbolic, Z-Alphanumeric, Z-Custom define the character alphabet used for the final string.",
        "Modes" to "Standard (balanced), Compact, Secure (forces encryption), Ultra Compact, Custom.",
        "Password Protection" to "When supplied, a key is derived via PBKDF2-HMAC-SHA256 and used with AES-GCM. Passwords are never stored.",
        "Examples" to "Encode “Hello Blitz!” with default settings, then decode the resulting string to recover the exact original text including Unicode.",
        "Format Specification" to "Binary frame: MAGIC(ZC) | VERSION | FLAGS | META(code/hash/lang/mode) | [SALT][IV] | PAYLOAD | HASH → Base-N.",
        "Security Notes" to "Offline-first. No network. Distinguish encoding vs hashing vs real encryption. Use strong passwords for sensitive data."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Documentation") },
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
            sections.forEach { (title, body) ->
                Text(title, color = Color.White, style = MaterialTheme.typography.titleMedium)
                Text(body, color = Color(0xFF8AA0C0), style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
