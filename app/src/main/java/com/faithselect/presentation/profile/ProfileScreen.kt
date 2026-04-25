package com.faithselect.presentation.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faithselect.domain.model.AppLanguage
import com.faithselect.domain.model.AppTheme
import com.faithselect.presentation.theme.FaithColors

@Composable
fun ProfileScreen(
    onNavigateToPaywall: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // ─── Header ──────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(FaithColors.NavyDeep, MaterialTheme.colorScheme.background)
                    )
                )
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()) {
                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(FaithColors.GoldPrimary.copy(alpha = 0.2f))
                        .border(2.dp, FaithColors.GoldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🙏", fontSize = 32.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.userName.ifEmpty { "Devotee" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FaithColors.GoldLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Subscription badge
                val badgeText = when {
                    uiState.isSubscribed -> "✦ Premium Member"
                    uiState.isTrialActive -> "🆓 Free Trial Active"
                    else -> "Subscribe to unlock"
                }
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (uiState.isSubscribed || uiState.isTrialActive)
                        FaithColors.GoldPrimary else MaterialTheme.colorScheme.error
                )
            }
        }

        // ─── Subscription section ─────────────────────────────────────────────
        if (!uiState.isSubscribed && !uiState.isTrialActive) {
            Card(
                onClick = onNavigateToPaywall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = FaithColors.GoldPrimary.copy(alpha = 0.1f)
                ),
                border = BorderStroke(1.dp, FaithColors.GoldPrimary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star, null,
                        tint = FaithColors.GoldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Upgrade to Premium",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = FaithColors.GoldPrimary
                        )
                        Text(
                            "₹99/month · 3-day free trial",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(Icons.Default.ChevronRight, null,
                        tint = FaithColors.GoldPrimary)
                }
            }
        }

        // ─── Settings Sections ────────────────────────────────────────────────
        Spacer(modifier = Modifier.height(8.dp))

        SectionLabel("Reading Preferences")

        // Language selector
        SettingsDropdown(
            icon = Icons.Default.Translate,
            title = "Display Language",
            value = uiState.language.displayName,
            options = AppLanguage.entries.map { it.displayName },
            onOptionSelected = { index ->
                viewModel.setLanguage(AppLanguage.entries[index])
            }
        )

        // Font size slider
        SettingsSlider(
            icon = Icons.Default.FormatSize,
            title = "Font Size",
            value = uiState.fontSize,
            valueRange = 12f..28f,
            onValueChange = { viewModel.setFontSize(it) }
        )

        SectionLabel("Appearance")

        // Theme selector
        SettingsDropdown(
            icon = Icons.Default.Palette,
            title = "Theme",
            value = uiState.theme.name.lowercase().replaceFirstChar { it.uppercase() }
                .replace("_", " "),
            options = AppTheme.entries.map {
                it.name.lowercase().replaceFirstChar { c -> c.uppercase() }
                    .replace("_", " ")
            },
            onOptionSelected = { index ->
                viewModel.setTheme(AppTheme.entries[index])
            }
        )

        SectionLabel("Subscription")

        SettingsRow(
            icon = Icons.Default.Restore,
            title = "Restore Purchases",
            subtitle = "Reconnect your subscription",
            onClick = { viewModel.restorePurchases() }
        )

        SectionLabel("Support")

        SettingsRow(
            icon = Icons.Default.PrivacyTip,
            title = "Privacy Policy",
            onClick = { /* Open browser */ }
        )
        SettingsRow(
            icon = Icons.Default.Description,
            title = "Terms of Service",
            onClick = { /* Open browser */ }
        )
        SettingsRow(
            icon = Icons.Default.Email,
            title = "Contact Support",
            subtitle = "support@faithselect.app",
            onClick = { /* Open email */ }
        )
        SettingsRow(
            icon = Icons.Default.Info,
            title = "App Version",
            subtitle = "1.0.0",
            onClick = {}
        )

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.secondary,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(icon, null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface)
            if (subtitle != null) {
                Text(subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(Icons.Default.ChevronRight, null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp))
    }
    HorizontalDivider(
        modifier = Modifier.padding(start = 72.dp, end = 16.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    )
}

@Composable
private fun SettingsDropdown(
    icon: ImageVector,
    title: String,
    value: String,
    options: List<String>,
    onOptionSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(icon, null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f))
        Text(value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.width(4.dp))
        Icon(Icons.Default.ExpandMore, null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp))

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(index)
                        expanded = false
                    }
                )
            }
        }
    }
    HorizontalDivider(
        modifier = Modifier.padding(start = 72.dp, end = 16.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    )
}

@Composable
private fun SettingsSlider(
    icon: ImageVector,
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Card(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(icon, null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f))
            Text("${value.toInt()}sp",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.padding(start = 56.dp),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary
            )
        )
    }
    HorizontalDivider(
        modifier = Modifier.padding(start = 72.dp, end = 16.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    )
}
