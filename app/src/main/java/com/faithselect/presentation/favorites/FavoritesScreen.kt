package com.faithselect.presentation.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faithselect.domain.model.AudioItem
import com.faithselect.domain.model.Verse
import com.faithselect.presentation.audio.formatDuration
import com.faithselect.presentation.theme.FaithColors
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

@Composable
fun FavoritesScreen(
    onVerseClick: (String) -> Unit,
    onAudioClick: (String) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "My Favorites",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
        )

        // Tab row: Verses | Audio
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.secondary,

        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        "Verses (${uiState.favoriteVerses.size})",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "Audio (${uiState.favoriteAudio.size})",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            )
        }

        if (selectedTab == 0) {
            FavoriteVerseList(
                verses = uiState.favoriteVerses,
                onVerseClick = onVerseClick,
                onRemove = { viewModel.removeFavoriteVerse(it) }
            )
        } else {
            FavoriteAudioList(
                audioItems = uiState.favoriteAudio,
                onAudioClick = onAudioClick,
                onRemove = { viewModel.removeFavoriteAudio(it) }
            )
        }
    }
}

@Composable
private fun FavoriteVerseList(
    verses: List<Verse>,
    onVerseClick: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    if (verses.isEmpty()) {
        EmptyState(message = "No favorite verses yet.\nTap ♡ while reading to save a verse.")
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(verses, key = { it.id }) { verse ->
                FavoriteVerseCard(
                    verse = verse,
                    onClick = { onVerseClick(verse.id) },
                    onRemove = { onRemove(verse.id) }
                )
            }
        }
    }
}

@Composable
private fun FavoriteVerseCard(verse: Verse, onClick: () -> Unit, onRemove: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Verse ${verse.chapterNumber}.${verse.verseNumber}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Remove favorite",
                        tint = FaithColors.GoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (verse.originalText.isNotEmpty()) {
                Text(
                    text = verse.originalText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = verse.englishText.ifEmpty { verse.hindiText },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FavoriteAudioList(
    audioItems: List<AudioItem>,
    onAudioClick: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    if (audioItems.isEmpty()) {
        EmptyState(message = "No favorite audio yet.\nSave audio to listen offline anytime.")
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(audioItems, key = { it.id }) { audio ->
                FavoriteAudioCard(
                    audio = audio,
                    onClick = { onAudioClick(audio.id) },
                    onRemove = { onRemove(audio.id) }
                )
            }
        }
    }
}

@Composable
private fun FavoriteAudioCard(audio: AudioItem, onClick: () -> Unit, onRemove: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.verticalGradient(listOf(FaithColors.NavyMid, FaithColors.NavyDeep))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🎵", fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = audio.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatDuration(audio.durationSeconds),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Remove",
                    tint = FaithColors.GoldPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🙏", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
