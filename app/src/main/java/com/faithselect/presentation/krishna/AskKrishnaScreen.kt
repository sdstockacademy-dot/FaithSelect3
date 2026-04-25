package com.faithselect.presentation.krishna

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.faithselect.data.krishna.KrishnaResponse
import com.faithselect.presentation.theme.FaithColors

// Quick suggestion chips shown when chat is empty
private val suggestions = listOf(
    "I am feeling stressed",
    "I have money problems",
    "I feel very lonely",
    "I am scared about my future",
    "I cannot control my anger",
    "I feel lost in life"
)

@Composable
fun AskKrishnaScreen(
    onBack: () -> Unit,
    viewModel: AskKrishnaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    // Auto-scroll to bottom when new message arrives
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    // Paywall bottom sheet
    if (uiState.showPaywall) {
        PaywallBottomSheet(
            onDismiss = { viewModel.dismissPaywall() }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FaithColors.NavyDeep)
    ) {
        // ─── Top Bar ──────────────────────────────────────────────────────
        AskKrishnaTopBar(
            questionsRemaining = uiState.questionsRemaining,
            isPremium = uiState.isPremium,
            onBack = onBack,
            onClear = { viewModel.clearChat() }
        )

        // ─── Chat Messages ─────────────────────────────────────────────────
        Box(modifier = Modifier.weight(1f)) {
            if (uiState.messages.isEmpty()) {
                KrishnaEmptyState(
                    suggestions = suggestions,
                    onSuggestionClick = { suggestion ->
                        inputText = suggestion
                        viewModel.sendMessage(suggestion)
                        inputText = ""
                    }
                )
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.messages, key = { it.id }) { message ->
                        if (message.isUser) {
                            UserMessageBubble(text = message.text)
                        } else {
                            KrishnaResponseBubble(response = message.krishnaResponse!!)
                        }
                    }

                    // Loading indicator
                    if (uiState.isLoading) {
                        item {
                            Row(
                                modifier = Modifier.padding(start = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("🦚", fontSize = 20.sp)
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = FaithColors.GoldPrimary,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    "Krishna is reflecting...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FaithColors.GoldPrimary.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ─── Input Bar ─────────────────────────────────────────────────────
        ChatInputBar(
            value = inputText,
            onValueChange = { inputText = it },
            onSend = {
                if (inputText.isNotBlank()) {
                    viewModel.sendMessage(inputText)
                    inputText = ""
                    focusManager.clearFocus()
                }
            },
            isLoading = uiState.isLoading
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AskKrishnaTopBar(
    questionsRemaining: Int,
    isPremium: Boolean,
    onBack: () -> Unit,
    onClear: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = FaithColors.NavyDeep
        ),
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back",
                    tint = FaithColors.TextLight)
            }
        },
        title = {
            Column {
                Text(
                    "Ask Krishna",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FaithColors.GoldLight
                )
                Text(
                    "Wisdom from Bhagavad Gita",
                    style = MaterialTheme.typography.labelSmall,
                    color = FaithColors.TextLight.copy(alpha = 0.5f)
                )
            }
        },
        actions = {
            // Questions remaining badge
            if (!isPremium) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            1.dp, FaithColors.GoldPrimary.copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (questionsRemaining > 0)
                            "$questionsRemaining left" else "Limit reached",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (questionsRemaining > 0)
                            FaithColors.GoldPrimary else Color.Red.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Text("✦", color = FaithColors.GoldPrimary, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
            }
            IconButton(onClick = onClear) {
                Icon(Icons.Default.Delete, "Clear chat",
                    tint = FaithColors.TextLight.copy(alpha = 0.5f))
            }
        }
    )
}

@Composable
private fun KrishnaEmptyState(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🦚", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Ask Krishna anything",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = FaithColors.GoldLight,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Share your problem and receive\nwisdom from the Bhagavad Gita",
            style = MaterialTheme.typography.bodyMedium,
            color = FaithColors.TextLight.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "Try one of these:",
            style = MaterialTheme.typography.labelMedium,
            color = FaithColors.GoldPrimary.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        // Suggestion chips
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            suggestions.forEach { suggestion ->
                SuggestionChip(
                    onClick = { onSuggestionClick(suggestion) },
                    label = {
                        Text(
                            suggestion,
                            style = MaterialTheme.typography.bodySmall,
                            color = FaithColors.TextLight
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = FaithColors.NavyMid
                    ),
                    border = SuggestionChipDefaults.suggestionChipBorder(
                        enabled = true,
                        borderColor = FaithColors.GoldPrimary.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun UserMessageBubble(text: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp, topEnd = 4.dp,
                        bottomStart = 16.dp, bottomEnd = 16.dp
                    )
                )
                .background(FaithColors.GoldPrimary.copy(alpha = 0.2f))
                .border(
                    1.dp,
                    FaithColors.GoldPrimary.copy(alpha = 0.4f),
                    RoundedCornerShape(
                        topStart = 16.dp, topEnd = 4.dp,
                        bottomStart = 16.dp, bottomEnd = 16.dp
                    )
                )
                .padding(14.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = FaithColors.TextLight
            )
        }
    }
}

@Composable
private fun KrishnaResponseBubble(response: KrishnaResponse) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        // Krishna avatar
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(FaithColors.GoldPrimary.copy(alpha = 0.1f))
                .border(1.dp, FaithColors.GoldPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🦚", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Verse reference
            Text(
                response.gitaVerse,
                style = MaterialTheme.typography.labelSmall,
                color = FaithColors.GoldPrimary,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            )

            // Gita insight
            ResponseSection(
                icon = "📖",
                label = "KRISHNA SAYS",
                text = response.gitaInsight,
                isHighlight = true
            )

            // Simple explanation
            ResponseSection(
                icon = "💡",
                label = "MEANING",
                text = response.simpleExplanation
            )

            // Action step
            ResponseSection(
                icon = "🎯",
                label = "YOUR ACTION TODAY",
                text = response.actionStep,
                isAction = true
            )
        }
    }
}

@Composable
private fun ResponseSection(
    icon: String,
    label: String,
    text: String,
    isHighlight: Boolean = false,
    isAction: Boolean = false
) {
    val bgColor = when {
        isHighlight -> FaithColors.NavyLight.copy(alpha = 0.8f)
        else -> FaithColors.NavyMid.copy(alpha = 0.5f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .then(
                if (isAction) Modifier.border(
                    1.dp,
                    FaithColors.GoldPrimary.copy(alpha = 0.4f),
                    RoundedCornerShape(12.dp)
                ) else Modifier
            )
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(icon, fontSize = 11.sp)
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = FaithColors.GoldPrimary,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (isHighlight) FaithColors.GoldLight else FaithColors.TextLight,
            lineHeight = 20.sp,
            fontStyle = if (isHighlight) FontStyle.Italic else FontStyle.Normal
        )
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FaithColors.NavyDeep)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    "Share what's on your heart...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FaithColors.TextLight.copy(alpha = 0.3f)
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = FaithColors.TextLight
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FaithColors.GoldPrimary.copy(alpha = 0.6f),
                unfocusedBorderColor = FaithColors.NavyLight,
                cursorColor = FaithColors.GoldPrimary,
                focusedContainerColor = FaithColors.NavyMid,
                unfocusedContainerColor = FaithColors.NavyMid
            ),
            shape = RoundedCornerShape(24.dp),
            maxLines = 4,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onSend() })
        )

        // Send button
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(FaithColors.GoldPrimary)
                .clickable(enabled = !isLoading) { onSend() },
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = FaithColors.NavyDeep,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = FaithColors.NavyDeep,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallBottomSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = FaithColors.NavyMid,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🙏", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Daily Limit Reached",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = FaithColors.GoldLight
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "You have used your 2 free questions for today.\nUpgrade to ask unlimited questions.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = FaithColors.TextLight.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Pricing
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, FaithColors.GoldPrimary, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "₹49/month",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = FaithColors.GoldPrimary
                    )
                    Text(
                        "Unlimited questions · Unlimited audio · Cancel anytime",
                        style = MaterialTheme.typography.labelSmall,
                        color = FaithColors.TextLight.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FaithColors.GoldPrimary,
                    contentColor = FaithColors.NavyDeep
                )
            ) {
                Text(
                    "Subscribe — ₹49/month",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onDismiss) {
                Text(
                    "Come back tomorrow for 2 more free questions",
                    style = MaterialTheme.typography.labelSmall,
                    color = FaithColors.TextLight.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
