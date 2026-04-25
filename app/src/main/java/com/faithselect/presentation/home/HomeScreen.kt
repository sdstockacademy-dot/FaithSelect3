package com.faithselect.presentation.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faithselect.domain.model.DailyContent
import com.faithselect.domain.model.Religion
import com.faithselect.presentation.theme.FaithColors

@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToDailyVerse: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToAudio: () -> Unit,
    onNavigateToAskKrishna: () -> Unit = {},
    onNavigateToMood: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // ─── Header ──────────────────────────────────────────────────────────
        item {
            HomeHeader(
                greeting = uiState.greeting,
                onSearchClick = onNavigateToSearch
            )
        }

        // ─── Daily Verse Card ────────────────────────────────────────────────
            item {
                // Daily verse hidden for now — will show when Firestore has data
            }

        // ─── Quick Access ────────────────────────────────────────────────────
        item {
            QuickAccessRow(
                onLibraryClick = onNavigateToLibrary,
                onAudioClick = onNavigateToAudio,
                onAskKrishnaClick = onNavigateToAskKrishna,
                onMoodClick = onNavigateToMood,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // ─── Section: Scriptures ─────────────────────────────────────────────
        item {
            SectionHeader(
                title = "Sacred Scriptures",
                onViewAllClick = onNavigateToLibrary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.religions) { religion ->
                    ReligionCard(religion = religion, onClick = onNavigateToLibrary)
                }
            }
        }

        // ─── Section: Featured Audio ──────────────────────────────────────────
        item {
            SectionHeader(
                title = "Featured Audio",
                onViewAllClick = onNavigateToAudio,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        items(uiState.featuredAudioTitles) { audioTitle ->
            FeaturedAudioRow(
                title = audioTitle,
                onClick = onNavigateToAudio,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun HomeHeader(greeting: String, onSearchClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = greeting,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Faith Select",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        IconButton(
            onClick = onSearchClick,
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DailyVerseCard(
    daily: DailyContent,
    language: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val verseText = when (language) {
        "hi" -> daily.hindiText
        "bn" -> daily.bengaliText
        else -> daily.englishText
    }.ifEmpty { daily.englishText }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(FaithColors.NavyDeep, FaithColors.NavyMid)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = FaithColors.GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Daily Verse",
                        style = MaterialTheme.typography.labelMedium,
                        color = FaithColors.GoldPrimary,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = verseText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = FaithColors.TextLight,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = daily.source,
                    style = MaterialTheme.typography.labelMedium,
                    color = FaithColors.GoldLight.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun QuickAccessRow(
    onLibraryClick: () -> Unit,
    onAudioClick: () -> Unit,
    onAskKrishnaClick: () -> Unit,
    onMoodClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Row 1: Ask Krishna + Mood (NEW)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessCard(
                icon = "🦚",
                title = "Ask Krishna",
                subtitle = "Get Gita wisdom",
                onClick = onAskKrishnaClick,
                modifier = Modifier.weight(1f)
            )
            QuickAccessCard(
                icon = "💭",
                title = "How I Feel",
                subtitle = "Mood guidance",
                onClick = onMoodClick,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        // Row 2: Read + Listen
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessCard(
                icon = "📖",
                title = "Read",
                subtitle = "Scriptures",
                onClick = onLibraryClick,
                modifier = Modifier.weight(1f)
            )
            QuickAccessCard(
                icon = "🎵",
                title = "Listen",
                subtitle = "Audio & Prayers",
                onClick = onAudioClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickAccessCard(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = icon, fontSize = 28.sp)
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ReligionCard(religion: Religion, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = getReligionEmoji(religion.name),
                fontSize = 36.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = religion.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = religion.nameHindi,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FeaturedAudioRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(FaithColors.GoldPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = FaithColors.GoldPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        TextButton(onClick = onViewAllClick) {
            Text(
                text = "View All",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

private fun getReligionEmoji(name: String): String = when (name.lowercase()) {
    "hinduism" -> "🕉️"
    "christianity" -> "✝️"
    "islam" -> "☪️"
    "buddhism" -> "☸️"
    "sikhism" -> "🪯"
    else -> "📿"
}
