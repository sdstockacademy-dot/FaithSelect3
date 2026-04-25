package com.faithselect.presentation.paywall

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faithselect.presentation.theme.FaithColors

@Composable
fun PaywallScreen(
    onSubscribed: () -> Unit,
    viewModel: PaywallViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Navigate away once subscribed
    LaunchedEffect(uiState.isSubscribed) {
        if (uiState.isSubscribed) onSubscribed()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(FaithColors.NavyDeep, FaithColors.NavyMid)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            // OM Symbol Header
            Text(text = "ॐ", fontSize = 64.sp, color = FaithColors.GoldPrimary)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Unlock Sacred Wisdom",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = FaithColors.GoldLight,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Full access to all scriptures, audio & teachings",
                style = MaterialTheme.typography.bodyLarge,
                color = FaithColors.TextLight.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Feature bullets
            FeatureList()

            Spacer(modifier = Modifier.height(32.dp))

            // Pricing Card
            PricingCard(
                price = uiState.priceString.ifEmpty { "₹99/month" },
                trialDays = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Subscribe Button
            Button(
                onClick = {
                    onSubscribed()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FaithColors.GoldPrimary,
                    contentColor = FaithColors.NavyDeep
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = FaithColors.NavyDeep,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Start 3-Day Free Trial",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Restore Purchases
            TextButton(
                onClick = { viewModel.restorePurchases() }
            ) {
                Text(
                    text = "Restore Purchases",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FaithColors.TextLight.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Error message
            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Legal disclaimer
            Text(
                text = "₹99/month after free trial. Cancel anytime.\n" +
                        "Subscription renews automatically. Terms apply.",
                style = MaterialTheme.typography.labelSmall,
                color = FaithColors.TextLight.copy(alpha = 0.4f),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FeatureList() {
    val features = listOf(
        Triple(Icons.Default.MenuBook,     "Complete Scriptures",    "Bhagavad Gita, Ramayan, Mahabharat & more"),
        Triple(Icons.Default.Headphones,   "Audio Library",          "Hanuman Chalisa, devotional stories & teachings"),
        Triple(Icons.Default.Translate,    "3 Languages",            "Hindi, Bengali & English translations"),
        Triple(Icons.Default.Notifications,"Daily Verse",            "Morning inspiration delivered to you"),
        Triple(Icons.Default.CloudDownload,"Offline Access",         "Download audio for offline listening"),
        Triple(Icons.Default.Favorite,     "Bookmarks",              "Save verses and audio to revisit anytime")
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        features.forEach { (icon, title, subtitle) ->
            FeatureRow(icon = icon, title = title, subtitle = subtitle)
        }
    }
}

@Composable
private fun FeatureRow(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(FaithColors.GoldPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = FaithColors.GoldPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = FaithColors.TextLight
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = FaithColors.TextLight.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun PricingCard(price: String, trialDays: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        FaithColors.GoldDeep.copy(alpha = 0.2f),
                        FaithColors.GoldPrimary.copy(alpha = 0.1f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(FaithColors.GoldDeep, FaithColors.GoldPrimary)
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$trialDays days FREE",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FaithColors.GoldPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "then $price",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Light,
                color = FaithColors.TextLight
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Cancel anytime before trial ends",
                style = MaterialTheme.typography.bodySmall,
                color = FaithColors.TextLight.copy(alpha = 0.5f)
            )
        }
    }
}
