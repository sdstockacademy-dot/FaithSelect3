package com.faithselect.presentation.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faithselect.presentation.theme.FaithColors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {

    // Animate in the logo and tagline
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "logoScale"
    )

    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(800))
        delay(1200)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        FaithColors.NavyDeep,
                        FaithColors.NavyMid
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(alpha.value)
                .scale(scale)
        ) {
            // OM Symbol / App Icon placeholder
            Text(
                text = "ॐ",
                fontSize = 80.sp,
                color = FaithColors.GoldPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Faith Select",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Light,
                color = FaithColors.GoldLight,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sacred Wisdom. Pure Knowledge.",
                style = MaterialTheme.typography.bodyMedium,
                color = FaithColors.TextLight.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                letterSpacing = 1.5.sp
            )
        }

        // App version at the bottom
        Text(
            text = "v1.0",
            style = MaterialTheme.typography.labelSmall,
            color = FaithColors.TextLight.copy(alpha = 0.3f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(alpha.value)
        )
    }
}
