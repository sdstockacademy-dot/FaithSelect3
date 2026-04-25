package com.faithselect.presentation.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.faithselect.presentation.theme.FaithColors
import kotlinx.coroutines.launch

data class OnboardingPage(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val gradientColors: List<Color>
)

private val onboardingPages = listOf(
    OnboardingPage(
        emoji = "📖",
        title = "Sacred Scriptures",
        subtitle = "Bhagavad Gita · Ramayan · Mahabharat",
        description = "Explore timeless wisdom from the world's most revered spiritual texts — in Hindi, Bengali, and English.",
        gradientColors = listOf(FaithColors.NavyDeep, FaithColors.NavyMid)
    ),
    OnboardingPage(
        emoji = "🎵",
        title = "Devotional Audio",
        subtitle = "Hanuman Chalisa · Stories · Teachings",
        description = "Listen to sacred chants, spiritual stories, and divine teachings. Play in the background while you go about your day.",
        gradientColors = listOf(FaithColors.NavyMid, Color(0xFF1A3050))
    ),
    OnboardingPage(
        emoji = "🌅",
        title = "Daily Inspiration",
        subtitle = "A verse for every morning",
        description = "Receive a curated verse each day to guide your thoughts, nourish your soul, and deepen your spiritual practice.",
        gradientColors = listOf(Color(0xFF1A3050), FaithColors.NavyDeep)
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == onboardingPages.size - 1

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            OnboardingPage(page = onboardingPages[pageIndex])
        }

        // Bottom controls overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page Indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                repeat(onboardingPages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .animateContentSize(tween(300))
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) FaithColors.GoldPrimary
                                else FaithColors.TextLight.copy(alpha = 0.3f)
                            )
                            .width(if (isSelected) 24.dp else 8.dp)
                    )
                }
            }

            // Primary CTA button
            Button(
                onClick = {
                    if (isLastPage) {
                        viewModel.completeOnboarding()
                        onComplete()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FaithColors.GoldPrimary,
                    contentColor = FaithColors.NavyDeep
                )
            ) {
                Text(
                    text = if (isLastPage) "Begin Your Journey" else "Continue",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Skip button
            if (!isLastPage) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FaithColors.TextLight.copy(alpha = 0.5f),
                    modifier = Modifier
                        .clickable {
                            viewModel.completeOnboarding()
                            onComplete()
                        }
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun OnboardingPage(page: OnboardingPage) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors = page.gradientColors)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .padding(bottom = 200.dp)
        ) {
            Text(
                text = page.emoji,
                fontSize = 72.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = FaithColors.GoldLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = page.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = FaithColors.GoldPrimary,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyLarge,
                color = FaithColors.TextLight.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )
        }
    }
}
