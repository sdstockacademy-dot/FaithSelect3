package com.faithselect.presentation.mood

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faithselect.presentation.theme.FaithColors

enum class MoodOption(
    val label: String,
    val emoji: String,
    val color: Color,
    val keyword: String
) {
    SAD      ("Sad",      "😢", Color(0xFF5B8DB8), "I am feeling very sad and unhappy"),
    ANGRY    ("Angry",    "😠", Color(0xFFD45C3A), "I am feeling so angry and frustrated"),
    ANXIOUS  ("Anxious",  "😰", Color(0xFFA855A1), "I am feeling very anxious and fearful"),
    CONFUSED ("Confused", "😕", Color(0xFF7A8FA6), "I am confused and don't know what to do"),
    STRESSED ("Stressed", "😫", Color(0xFF8B7355), "I am very stressed and overwhelmed"),
    LONELY   ("Lonely",   "😔", Color(0xFF4A6572), "I am feeling very lonely and isolated"),
    GRATEFUL ("Grateful", "🙏", Color(0xFF5B9E4A), "I am feeling grateful and at peace today")
}

@Composable
fun MoodScreen(
    onBack: () -> Unit,
    viewModel: MoodViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FaithColors.NavyDeep)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = FaithColors.TextLight)
            }
            Text(
                "How I Feel",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FaithColors.GoldLight
            )
        }

        if (uiState.selectedMood == null) {
            // Mood selection grid
            MoodSelectionGrid(
                onMoodSelected = { viewModel.selectMood(it) }
            )
        } else {
            // Show guidance for selected mood
            MoodGuidanceView(
                mood = uiState.selectedMood!!,
                response = uiState.krishnaResponse,
                onSelectDifferentMood = { viewModel.clearMood() }
            )
        }
    }
}

@Composable
private fun MoodSelectionGrid(onMoodSelected: (MoodOption) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "How are you feeling\nright now?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = FaithColors.TextLight,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Krishna will guide you based on your mood",
            style = MaterialTheme.typography.bodyMedium,
            color = FaithColors.TextLight.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        // 2-column grid of moods
        val rows = MoodOption.values().toList().chunked(2)
        rows.forEach { rowMoods ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowMoods.forEach { mood ->
                    MoodCard(
                        mood = mood,
                        onClick = { onMoodSelected(mood) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill empty space in last row if odd number
                if (rowMoods.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun MoodCard(
    mood: MoodOption,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = mood.color.copy(alpha = 0.15f)
        ),
        border = BorderStroke(1.dp, mood.color.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(mood.emoji, fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                mood.label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = mood.color
            )
        }
    }
}

@Composable
private fun MoodGuidanceView(
    mood: MoodOption,
    response: com.faithselect.data.krishna.KrishnaResponse?,
    onSelectDifferentMood: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        // Mood header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(mood.color.copy(alpha = 0.15f))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(mood.emoji, fontSize = 36.sp)
            Column {
                Text(
                    "Feeling ${mood.label}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = mood.color
                )
                Text(
                    "Here is Krishna's guidance for you",
                    style = MaterialTheme.typography.bodySmall,
                    color = FaithColors.TextLight.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (response != null) {
            // Verse
            Text(
                response.gitaVerse,
                style = MaterialTheme.typography.labelMedium,
                color = FaithColors.GoldPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Gita insight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(FaithColors.NavyLight.copy(alpha = 0.8f))
                    .padding(16.dp)
            ) {
                Text(
                    text = "\"${response.gitaInsight}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    color = FaithColors.GoldLight,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 26.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Explanation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(FaithColors.NavyMid.copy(alpha = 0.6f))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        "💡 WHAT THIS MEANS",
                        style = MaterialTheme.typography.labelSmall,
                        color = FaithColors.GoldPrimary,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        response.simpleExplanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = FaithColors.TextLight,
                        lineHeight = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action step
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(FaithColors.GoldPrimary.copy(alpha = 0.1f))
                    .border(1.dp, FaithColors.GoldPrimary.copy(alpha = 0.4f),
                        RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        "🎯 YOUR ACTION TODAY",
                        style = MaterialTheme.typography.labelSmall,
                        color = FaithColors.GoldPrimary,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        response.actionStep,
                        style = MaterialTheme.typography.bodyMedium,
                        color = FaithColors.TextLight,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Try different mood button
        OutlinedButton(
            onClick = onSelectDifferentMood,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, FaithColors.GoldPrimary.copy(alpha = 0.4f))
        ) {
            Text(
                "Select a different mood",
                color = FaithColors.GoldPrimary
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
