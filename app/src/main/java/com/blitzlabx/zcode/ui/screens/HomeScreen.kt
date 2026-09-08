package com.blitzlabx.zcode.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blitzlabx.zcode.ui.theme.*
import com.blitzlabx.zcode.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigate: (String) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val stats by viewModel.stats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Z", color = ZBlueGlow, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                            Text("-Code", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                        }
                        Text("Created by Blitz", color = Color(0xFF6A8AAA), fontSize = 12.sp)
                    }
                },
                actions = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = ZBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZBackground)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = ZSurface) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("history") },
                    icon = { Icon(Icons.Default.History, null) },
                    label = { Text("History") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("languages") },
                    icon = { Icon(Icons.Default.Translate, null) },
                    label = { Text("Languages") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("settings") },
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text("Settings") }
                )
            }
        },
        containerColor = ZBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Quick Actions", color = Color(0xFF8AA0C0), fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard(
                    title = "Generate",
                    icon = Icons.Default.AutoAwesome,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("generator") }
                )
                QuickActionCard(
                    title = "Translate",
                    icon = Icons.Default.Translate,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("translator") }
                )
            }
            Spacer(Modifier.height(24.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = ZSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.OfflineBolt, null, tint = ZSuccess, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Offline Mode", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text("All features available offline", color = Color(0xFF8AA0C0), fontSize = 13.sp)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatItem("Total", stats.first.toString())
                StatItem("Saved", stats.second.toString())
                StatItem("History", stats.third.toString())
            }
        }
    }
}

@Composable
private fun QuickActionCard(title: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ZSurface)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(ZSurface, ZSurfaceVariant))
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(icon, null, tint = ZBlue, modifier = Modifier.size(36.dp))
                Spacer(Modifier.height(8.dp))
                Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = ZBlueGlow, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color(0xFF6A8AAA), fontSize = 13.sp)
    }
}
