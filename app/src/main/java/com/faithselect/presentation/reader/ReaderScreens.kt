package com.faithselect.presentation.reader

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faithselect.domain.model.AppLanguage
import com.faithselect.domain.model.Verse
import com.faithselect.presentation.library.FaithTopBar
import com.faithselect.presentation.library.LoadingCenter
import com.faithselect.presentation.theme.FaithColors

// ─── Verse List Screen ────────────────────────────────────────────────────────

@Composable
fun VerseListScreen(
    chapterId: String,
    chapterTitle: String,
    onVerseClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: VerseListViewModel = hiltViewModel()
) {
    LaunchedEffect(chapterId) { viewModel.loadVerses(chapterId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        FaithTopBar(title = chapterTitle, onBack = onBack)

        if (uiState.isLoading) {
            LoadingCenter()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.verses) { verse ->
                    VerseListItem(
                        verse = verse,
                        language = uiState.language,
                        onClick = { onVerseClick(verse.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun VerseListItem(
    verse: Verse,
    language: AppLanguage,
    onClick: () -> Unit
) {
    val previewText = when (language) {
        AppLanguage.HINDI   -> verse.hindiText
        AppLanguage.BENGALI -> verse.bengaliText
        else                -> verse.englishText
    }.take(80) + if (verse.englishText.length > 80) "…" else ""

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Verse number badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${verse.verseNumber}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                // Sanskrit/original text snippet
                if (verse.originalText.isNotEmpty()) {
                    Text(
                        text = verse.originalText.take(60) + "…",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Text(
                    text = previewText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // Audio indicator
                if (verse.audioUrl.isNotEmpty()) {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = FaithColors.GoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Audio available",
                            style = MaterialTheme.typography.labelSmall,
                            color = FaithColors.GoldPrimary
                        )
                    }
                }
            }
        }
    }
}

// ─── Verse Reader Screen (Full detail) ────────────────────────────────────────

@Composable
fun VerseReaderScreen(
    verseId: String,
    onAudioPlay: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: VerseReaderViewModel = hiltViewModel()
) {
    LaunchedEffect(verseId) { viewModel.loadVerse(verseId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar with favorite button
        ReaderTopBar(
            isFavorited = uiState.isFavorited,
            fontSize = uiState.fontSize,
            onFavoriteToggle = { viewModel.toggleFavorite() },
            onFontIncrease = { viewModel.increaseFontSize() },
            onFontDecrease = { viewModel.decreaseFontSize() },
            onBack = onBack
        )

        if (uiState.isLoading) {
            LoadingCenter()
        } else {
            uiState.verse?.let { verse ->
                // Language Tab selector
                LanguageTabBar(
                    selectedLanguage = uiState.selectedLanguage,
                    onLanguageSelect = { viewModel.selectLanguage(it) }
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    // Verse reference header
                    Text(
                        text = "Verse ${verse.chapterNumber}.${verse.verseNumber}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Original/Sanskrit text
                    if (verse.originalText.isNotEmpty()) {
                        VerseTextCard(
                            label = "Original",
                            text = verse.originalText,
                            fontSize = uiState.fontSize,
                            isOriginal = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Translation in selected language
                    val translationText = when (uiState.selectedLanguage) {
                        AppLanguage.HINDI   -> verse.hindiText
                        AppLanguage.BENGALI -> verse.bengaliText
                        else                -> verse.englishText
                    }

                    val meaningText = when (uiState.selectedLanguage) {
                        AppLanguage.HINDI   -> verse.hindiMeaning
                        AppLanguage.BENGALI -> verse.bengaliMeaning
                        else                -> verse.englishMeaning
                    }

                    if (translationText.isNotEmpty()) {
                        VerseTextCard(
                            label = "Translation",
                            text = translationText,
                            fontSize = uiState.fontSize,
                            isOriginal = false
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Meaning / Commentary
                    if (meaningText.isNotEmpty()) {
                        CommentaryCard(text = meaningText, fontSize = uiState.fontSize)
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Audio play button
                    if (verse.audioUrl.isNotEmpty()) {
                        AudioPlayButton(
                            isPlaying = uiState.isAudioPlaying,
                            onClick = {
                                viewModel.toggleAudio(verse.audioUrl)
                                onAudioPlay(verse.audioUrl)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun ReaderTopBar(
    isFavorited: Boolean,
    fontSize: Float,
    onFavoriteToggle: () -> Unit,
    onFontIncrease: () -> Unit,
    onFontDecrease: () -> Unit,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurface)
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onFontDecrease) {
            Icon(Icons.Default.TextDecrease, "Decrease font", tint = MaterialTheme.colorScheme.onSurface)
        }
        IconButton(onClick = onFontIncrease) {
            Icon(Icons.Default.TextIncrease, "Increase font", tint = MaterialTheme.colorScheme.onSurface)
        }
        IconButton(onClick = onFavoriteToggle) {
            Icon(
                imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorited) FaithColors.GoldPrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun LanguageTabBar(
    selectedLanguage: AppLanguage,
    onLanguageSelect: (AppLanguage) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        AppLanguage.entries.forEach { language ->
            val isSelected = language == selectedLanguage
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.secondary
                        else androidx.compose.ui.graphics.Color.Transparent
                    )
                    .clickable { onLanguageSelect(language) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = language.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.onSecondary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun VerseTextCard(
    label: String,
    text: String,
    fontSize: Float,
    isOriginal: Boolean
) {
    Column {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isOriginal)
                        Brush.verticalGradient(listOf(FaithColors.NavyDeep, FaithColors.NavyMid))
                    else
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                )
                .padding(16.dp)
        ) {
            Text(
                text = text,
                fontSize = fontSize.sp,
                lineHeight = (fontSize * 1.7f).sp,
                color = if (isOriginal) FaithColors.GoldLight else MaterialTheme.colorScheme.onSurface,
                textAlign = if (isOriginal) TextAlign.Center else TextAlign.Start,
                fontStyle = if (isOriginal) FontStyle.Italic else FontStyle.Normal
            )
        }
    }
}

@Composable
private fun CommentaryCard(text: String, fontSize: Float) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = FaithColors.GoldPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "MEANING",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.5.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            fontSize = (fontSize - 1f).sp,
            lineHeight = ((fontSize - 1f) * 1.8f).sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun AudioPlayButton(isPlaying: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FaithColors.GoldPrimary,
            contentColor = FaithColors.NavyDeep
        )
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isPlaying) "Stop Audio" else "Play Verse Audio",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}
