package com.faithselect.presentation.audio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faithselect.domain.model.AudioItem
import com.faithselect.domain.model.AudioCategory
import com.faithselect.presentation.library.LoadingCenter
import com.faithselect.presentation.theme.FaithColors

// ─── Audio List Screen ────────────────────────────────────────────────────────

@Composable
fun AudioListScreen(
    onAudioClick: (String) -> Unit,
    viewModel: AudioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf<AudioCategory?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "Audio Library",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
        )

        // Category filter chips
        CategoryFilterRow(
            selectedCategory = selectedCategory,
            onCategorySelected = {
                selectedCategory = if (selectedCategory == it) null else it
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.isLoading) {
            LoadingCenter()
        } else {
            val filtered = if (selectedCategory == null) uiState.audioItems
                           else uiState.audioItems.filter { it.category == selectedCategory }

            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No audio available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered) { audio ->
                        AudioListItem(audio = audio, onClick = { onAudioClick(audio.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterRow(
    selectedCategory: AudioCategory?,
    onCategorySelected: (AudioCategory) -> Unit
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AudioCategory.entries.forEach { category ->
            val isSelected = category == selectedCategory
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = category.displayName(),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.secondary
                )
            )
        }
    }
}

@Composable
private fun AudioListItem(audio: AudioItem, onClick: () -> Unit) {
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
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(FaithColors.NavyMid, FaithColors.NavyDeep)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = audio.category.emoji(), fontSize = 24.sp)
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
                if (audio.titleHindi.isNotEmpty()) {
                    Text(
                        text = audio.titleHindi,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "${formatDuration(audio.durationSeconds)} · ${audio.category.displayName()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Icon(
                imageVector = Icons.Default.PlayCircle,
                contentDescription = "Play",
                tint = FaithColors.GoldPrimary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

// ─── Audio Player Screen ──────────────────────────────────────────────────────

@Composable
fun AudioPlayerScreen(
    audioId: String,
    onBack: () -> Unit,
    viewModel: AudioPlayerViewModel = hiltViewModel()
) {
    LaunchedEffect(audioId) { viewModel.loadAudio(audioId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(FaithColors.NavyDeep, Color(0xFF0A1628))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back button
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Close",
                        tint = FaithColors.TextLight,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Album Art (emoji-based)
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(FaithColors.NavyMid, FaithColors.NavyLight)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.audioItem?.category?.emoji() ?: "🎵",
                    fontSize = 80.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Title
            Text(
                text = uiState.audioItem?.title ?: "Loading...",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = FaithColors.GoldLight,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            if (uiState.audioItem?.titleHindi?.isNotEmpty() == true) {
                Text(
                    text = uiState.audioItem?.titleHindi ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FaithColors.TextLight.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Progress Slider
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = if (uiState.durationMs > 0)
                        uiState.currentPositionMs.toFloat() / uiState.durationMs
                    else 0f,
                    onValueChange = { viewModel.seekTo(it) },
                    colors = SliderDefaults.colors(
                        thumbColor = FaithColors.GoldPrimary,
                        activeTrackColor = FaithColors.GoldPrimary,
                        inactiveTrackColor = FaithColors.TextLight.copy(alpha = 0.2f)
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatDuration((uiState.currentPositionMs / 1000).toInt()),
                        style = MaterialTheme.typography.labelSmall,
                        color = FaithColors.TextLight.copy(alpha = 0.5f)
                    )
                    Text(
                        text = formatDuration((uiState.durationMs / 1000).toInt()),
                        style = MaterialTheme.typography.labelSmall,
                        color = FaithColors.TextLight.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Playback Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skip back 10s
                IconButton(onClick = { viewModel.skipBack() }) {
                    Icon(
                        Icons.Default.Replay10,
                        contentDescription = "Replay 10s",
                        tint = FaithColors.TextLight,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Play / Pause
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(FaithColors.GoldPrimary)
                        .clickable { viewModel.togglePlayPause() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                        tint = FaithColors.NavyDeep,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Skip forward 10s
                IconButton(onClick = { viewModel.skipForward() }) {
                    Icon(
                        Icons.Default.Forward10,
                        contentDescription = "Forward 10s",
                        tint = FaithColors.TextLight,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Download button
            if (uiState.audioItem?.isDownloadable == true) {
                OutlinedButton(
                    onClick = { viewModel.downloadAudio() },
                    border = BorderStroke(1.dp, FaithColors.GoldPrimary.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = FaithColors.GoldPrimary
                    )
                ) {
                    Icon(
                        imageVector = if (uiState.isDownloaded)
                            Icons.Default.CheckCircle else Icons.Default.CloudDownload,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.isDownloaded) "Downloaded" else "Download for Offline",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

fun AudioCategory.emoji(): String = when (this) {
    AudioCategory.PRAYER    -> "🙏"
    AudioCategory.STORY     -> "📖"
    AudioCategory.TEACHING  -> "🎓"
    AudioCategory.MEDITATION-> "🧘"
    AudioCategory.VERSE     -> "📜"
}

fun AudioCategory.displayName(): String = when (this) {
    AudioCategory.PRAYER    -> "Prayer"
    AudioCategory.STORY     -> "Story"
    AudioCategory.TEACHING  -> "Teaching"
    AudioCategory.MEDITATION-> "Meditation"
    AudioCategory.VERSE     -> "Verse"
}

fun formatDuration(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
