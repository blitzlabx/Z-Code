package com.blitzlabx.zcode.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blitzlabx.zcode.ui.theme.ZBackground
import com.blitzlabx.zcode.ui.theme.ZBlue
import com.blitzlabx.zcode.ui.theme.ZSurface
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val pages = listOf(
        Triple(Icons.Default.Lock, "Secure & Private", "Your messages, your data. Works completely offline."),
        Triple(Icons.Default.Compress, "Compact Encoding", "Short, random-looking strings. Not normal text."),
        Triple(Icons.Default.Tune, "Full Control", "Choose your settings. Customize your experience.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ZSurface),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.size(160.dp)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            pages[page].first,
                            contentDescription = null,
                            tint = ZBlue,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
                Spacer(Modifier.height(32.dp))
                Text(
                    pages[page].second,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    pages[page].third,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF8AA0C0),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(16.dp)) {
            repeat(3) { i ->
                Box(
                    Modifier
                        .padding(4.dp)
                        .size(if (pagerState.currentPage == i) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (pagerState.currentPage == i) ZBlue else Color(0xFF3A4A6A))
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onFinished) {
                Text("Skip", color = Color(0xFF6A8AAA))
            }
            Button(
                onClick = {
                    if (pagerState.currentPage < 2) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else onFinished()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ZBlue)
            ) {
                Text(if (pagerState.currentPage < 2) "Next" else "Get Started", color = Color.Black)
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}
