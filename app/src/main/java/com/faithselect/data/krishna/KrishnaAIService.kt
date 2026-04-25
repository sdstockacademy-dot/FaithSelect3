package com.faithselect.data.krishna

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * KrishnaAIService — 100% FREE, 100% OFFLINE rule-based AI.
 *
 * NO paid API. NO internet needed for AI responses.
 * Works by matching keywords in the user's question
 * to one of 20 handcrafted Bhagavad Gita-based responses.
 *
 * HOW IT WORKS:
 * 1. User types a problem
 * 2. We scan their text for known keywords
 * 3. We find the best matching response from JSON
 * 4. We return Gita verse + explanation + action step
 *
 * TO ADD MORE RESPONSES:
 * Just add more entries to assets/krishna_responses.json
 * No code changes needed!
 */
@Singleton
class KrishnaAIService @Inject constructor(
    private val context: Context
) {
    private var responses: List<KrishnaResponseData> = emptyList()
    private var defaultResponse: KrishnaResponseData? = null
    private val json = Json { ignoreUnknownKeys = true }

    init {
        loadResponses()
    }

    /** Load JSON from assets — fast, works offline */
    private fun loadResponses() {
        try {
            val jsonString = context.assets.open("krishna_responses.json")
                .bufferedReader()
                .use { it.readText() }

            val data = json.decodeFromString<KrishnaResponsesJson>(jsonString)
            responses = data.responses
            defaultResponse = data.default_response
        } catch (e: Exception) {
            // Fallback hardcoded response if JSON fails
            defaultResponse = KrishnaResponseData(
                id = 0,
                keywords = emptyList(),
                emotion = "default",
                gita_verse = "Bhagavad Gita 2.47",
                gita_insight = "You have the right to perform your duties, but not to the fruits of your actions.",
                simple_explanation = "Focus on what you can control — your effort. Let go of what you cannot control — the outcome.",
                action_step = "Take one small positive action today. Small steps lead to big changes."
            )
        }
    }

    /**
     * Main function: get Krishna's response for any user message.
     * Completely free, instant, works offline.
     */
    fun getResponse(userMessage: String): KrishnaResponse {
        val lower = userMessage.lowercase().trim()

        // Step 1: Keyword matching — most accurate
        for (response in responses) {
            for (keyword in response.keywords) {
                if (lower.contains(keyword.lowercase())) {
                    return response.toKrishnaResponse()
                }
            }
        }

        // Step 2: Emotion detection from writing style
        val detectedEmotion = detectEmotion(lower)
        if (detectedEmotion != null) {
            val match = responses.find { it.emotion == detectedEmotion }
            if (match != null) return match.toKrishnaResponse()
        }

        // Step 3: Default response — always meaningful
        return defaultResponse?.toKrishnaResponse() ?: KrishnaResponse(
            gitaVerse = "Bhagavad Gita 2.47",
            gitaInsight = "You have the right to perform your duties, but not to the fruits of your actions.",
            simpleExplanation = "Focus on your effort, not the outcome. Krishna is always with you.",
            actionStep = "Take one small positive action right now."
        )
    }

    /** Detect emotion from tone words when no keyword matched */
    private fun detectEmotion(message: String): String? {
        val emotionHints = mapOf(
            "stress"           to listOf("can't", "too much", "too many", "exhausted", "tired of"),
            "fear"             to listOf("what if", "might lose", "could happen", "scared that"),
            "sadness"          to listOf("miss", "hurts", "crying", "lost him", "lost her"),
            "anger"            to listOf("hate", "how could", "why would", "so unfair"),
            "confusion"        to listOf("what should i", "don't know what", "not sure", "help me decide"),
            "financial_stress" to listOf("can't afford", "no money", "pay bills", "in debt"),
            "loneliness"       to listOf("nobody", "no one", "all alone", "no friends")
        )

        for ((emotion, hints) in emotionHints) {
            for (hint in hints) {
                if (message.contains(hint)) return emotion
            }
        }
        return null
    }

    /** Get today's daily guidance verse (cycles through 7 entries) */
    fun getDailyGuidance(): DailyGuidance {
        val dayOfYear = java.util.Calendar.getInstance()
            .get(java.util.Calendar.DAY_OF_YEAR)
        val entries = dailyGuidanceList
        return entries[dayOfYear % entries.size]
    }
}

// ─── Data Models ──────────────────────────────────────────────────────────────

/** The structured response Krishna gives */
data class KrishnaResponse(
    val gitaVerse: String,
    val gitaInsight: String,
    val simpleExplanation: String,
    val actionStep: String
)

/** Daily guidance entry */
data class DailyGuidance(
    val verse: String,
    val sanskrit: String,
    val translation: String,
    val message: String,
    val affirmation: String
)

// ─── JSON Serialization Models ────────────────────────────────────────────────

@Serializable
data class KrishnaResponsesJson(
    val responses: List<KrishnaResponseData>,
    val default_response: KrishnaResponseData
)

@Serializable
data class KrishnaResponseData(
    val id: Int = 0,
    val keywords: List<String> = emptyList(),
    val emotion: String = "",
    val gita_verse: String = "",
    val gita_insight: String = "",
    val simple_explanation: String = "",
    val action_step: String = ""
) {
    fun toKrishnaResponse() = KrishnaResponse(
        gitaVerse = gita_verse,
        gitaInsight = gita_insight,
        simpleExplanation = simple_explanation,
        actionStep = action_step
    )
}

// ─── Daily Guidance Data (hardcoded — no internet needed) ─────────────────────
val dailyGuidanceList = listOf(
    DailyGuidance(
        verse = "Bhagavad Gita 2.47",
        sanskrit = "कर्मण्येवाधिकारस्ते मा फलेषु कदाचन",
        translation = "You have a right to perform your duties, but you are not entitled to the fruits.",
        message = "Today, focus only on your effort. Let go of the outcome. Do your best and trust the process.",
        affirmation = "I give my best effort and release the rest to the universe."
    ),
    DailyGuidance(
        verse = "Bhagavad Gita 2.14",
        sanskrit = "मात्रास्पर्शास्तु कौन्तेय शीतोष्णसुखदुःखदाः",
        translation = "The contacts of the senses give rise to cold, heat, pleasure and pain. They are impermanent.",
        message = "Whatever pain you feel today is temporary. Like weather, emotions change. Be patient with yourself.",
        affirmation = "This feeling is temporary. I will get through this."
    ),
    DailyGuidance(
        verse = "Bhagavad Gita 6.5",
        sanskrit = "उद्धरेदात्मनात्मानं नात्मानमवसादयेत्",
        translation = "Elevate yourself through the power of your mind, and do not degrade yourself.",
        message = "You are your own best friend and worst enemy. Choose kind thoughts about yourself today.",
        affirmation = "I choose to lift myself up with loving thoughts and actions."
    ),
    DailyGuidance(
        verse = "Bhagavad Gita 18.66",
        sanskrit = "सर्वधर्मान्परित्यज्य मामेकं शरणं व्रज",
        translation = "Abandon all varieties of dharma and simply surrender unto Me. Do not fear.",
        message = "Today, surrender your worries. You don't have to carry everything alone. Let go and trust.",
        affirmation = "I release my burdens and trust that I am guided and protected."
    ),
    DailyGuidance(
        verse = "Bhagavad Gita 12.13",
        sanskrit = "अद्वेष्टा सर्वभूतानां मैत्रः करुण एव च",
        translation = "One who is not envious but is a kind friend to all living entities is very dear to Me.",
        message = "Practice kindness today — to yourself and one other person. A small act of kindness creates ripples.",
        affirmation = "I am kind to myself and others. Kindness is my strength."
    ),
    DailyGuidance(
        verse = "Bhagavad Gita 6.19",
        sanskrit = "यथा दीपो निवातस्थो नेङ्गते सोपमा स्मृता",
        translation = "Just as a lamp in a windless place does not flicker, so the yogi's mind does not flicker.",
        message = "Find 5 minutes of stillness today. Even in chaos, you can find the calm center within you.",
        affirmation = "I am still and centered no matter what storms surround me."
    ),
    DailyGuidance(
        verse = "Bhagavad Gita 3.19",
        sanskrit = "तस्मादसक्तः सततं कार्यं कर्म समाचर",
        translation = "Without attachment, perform always the work that has to be done.",
        message = "Do your work today without worrying about what others think. Work for the joy of doing it well.",
        affirmation = "I work with full dedication and without the need for approval."
    )
)
