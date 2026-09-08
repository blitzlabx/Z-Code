package com.blitzlabx.zcode.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blitzlabx.zcode.ui.theme.ZBackground
import com.blitzlabx.zcode.ui.theme.ZBlue
import com.blitzlabx.zcode.ui.theme.ZBlueGlow
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var startAnim by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0.6f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f),
        label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(800),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        startAnim = true
        delay(1800)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(ZBackground, Color(0xFF0D1526), ZBackground)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Z",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = ZBlueGlow,
                modifier = Modifier
                    .scale(scale)
                    .alpha(alpha)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Z-Code",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = ZBlue,
                modifier = Modifier.alpha(alpha)
            )
            Text(
                text = "Created by Blitz",
                fontSize = 14.sp,
                color = Color(0xFF6A8AAA),
                modifier = Modifier.alpha(alpha * 0.8f)
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "blitzlabx",
                fontSize = 12.sp,
                color = Color(0xFF4A6A8A),
                modifier = Modifier.alpha(alpha * 0.6f)
            )
        }
    }
}
