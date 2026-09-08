package com.blitzlabx.zcode.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.blitzlabx.zcode.ui.theme.ZBackground
import com.blitzlabx.zcode.ui.theme.ZBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawerScreen(onNavigate: (String) -> Unit, onClose: () -> Unit) {
    val items = listOf(
        Triple(Icons.Default.AutoAwesome, "Generator", "generator"),
        Triple(Icons.Default.Translate, "Translator", "translator"),
        Triple(Icons.Default.History, "History", "history"),
        Triple(Icons.Default.Language, "Languages", "languages"),
        Triple(Icons.Default.Tune, "Modes", "modes"),
        Triple(Icons.Default.Settings, "Settings", "settings"),
        Triple(Icons.Default.Description, "Documentation", "documentation"),
        Triple(Icons.Default.Info, "About", "about")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Z-Code", color = ZBlue) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZBackground)
            )
        },
        containerColor = ZBackground
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            items.forEach { (icon, title, route) ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(route) }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(icon, null, tint = ZBlue)
                    Text(title, color = Color.White)
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("blitzlabx", color = Color(0xFF5A6A8A))
            Text("Telegram: https://t.me/blitzmax", color = Color(0xFF5A6A8A), style = MaterialTheme.typography.bodySmall)
        }
    }
}
