package com.blitzlabx.zcode.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blitzlabx.zcode.ui.theme.ZBackground
import com.blitzlabx.zcode.ui.theme.ZBlue
import com.blitzlabx.zcode.ui.theme.ZBlueGlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
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
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Z", fontSize = 56.sp, fontWeight = FontWeight.Bold, color = ZBlueGlow)
            Text("Z-Code", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = ZBlue)
            Text("Created by Blitz", color = Color(0xFF8AA0C0))
            Spacer(Modifier.height(24.dp))
            Text("Version 1.0.0", color = Color.White)
            Text("Author: Blitz", color = Color(0xFF8AA0C0))
            Text("Brand: blitzlabx", color = Color(0xFF8AA0C0))
            Text("Telegram: @blitzlabx / https://t.me/blitzmax", color = Color(0xFF8AA0C0))
            Spacer(Modifier.height(16.dp))
            Text("Compact Communication\nLimitless Possibilities.", color = Color(0xFF6A8AAA))
        }
    }
}
