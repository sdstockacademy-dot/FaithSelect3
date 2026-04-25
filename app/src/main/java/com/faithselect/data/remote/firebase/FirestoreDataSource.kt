package com.faithselect.data.remote.firebase

import com.faithselect.domain.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase Firestore data source.
 *
 * Firestore Collection Structure:
 * ─ religions/
 *     {religionId}/
 *         id, name, nameHindi, nameBengali, iconUrl, description, isActive, sortOrder
 *
 * ─ scriptures/
 *     {scriptureId}/
 *         id, religionId, title, titleHindi, titleBengali, description, coverImageUrl,
 *         totalChapters, isActive, sortOrder
 *
 * ─ chapters/
 *     {chapterId}/
 *         id, scriptureId, religionId, chapterNumber, title, titleHindi, titleBengali,
 *         totalVerses, summary
 *
 * ─ verses/
 *     {verseId}/
 *         id, chapterId, scriptureId, religionId, verseNumber, chapterNumber,
 *         originalText, hindiText, bengaliText, englishText,
 *         hindiMeaning, bengaliMeaning, englishMeaning, audioUrl, tags[]
 *
 * ─ audio/
 *     {audioId}/
 *         id, religionId, scriptureId, title, titleHindi, titleBengali, description,
 *         audioUrl, coverImageUrl, durationSeconds, category, isDownloadable
 *
 * ─ daily_content/
 *     {date "yyyy-MM-dd"}/
 *         id, date, verseId, originalText, hindiText, bengaliText, englishText,
 *         source, backgroundImageUrl
 */
@Singleton
class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // ─── Religions ────────────────────────────────────────────────────────────

    fun getReligions(): Flow<List<Religion>> = callbackFlow {
        val listener = firestore.collection("religions")
            .whereEqualTo("isActive", true)
            .orderBy("sortOrder", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val religions = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Religion::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(religions)
            }
        awaitClose { listener.remove() }
    }

    // ─── Scriptures ──────────────────────────────────────────────────────────

    fun getScripturesByReligion(religionId: String): Flow<List<Scripture>> = callbackFlow {
        val listener = firestore.collection("scriptures")
            .whereEqualTo("religionId", religionId)
            .whereEqualTo("isActive", true)
            .orderBy("sortOrder", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val scriptures = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Scripture::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(scriptures)
            }
        awaitClose { listener.remove() }
    }

    // ─── Chapters ────────────────────────────────────────────────────────────

    fun getChaptersByScripture(scriptureId: String): Flow<List<Chapter>> = callbackFlow {
        val listener = firestore.collection("chapters")
            .whereEqualTo("scriptureId", scriptureId)
            .orderBy("chapterNumber", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val chapters = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Chapter::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(chapters)
            }
        awaitClose { listener.remove() }
    }

    // ─── Verses ──────────────────────────────────────────────────────────────

    fun getVersesByChapter(chapterId: String): Flow<List<Verse>> = callbackFlow {
        val listener = firestore.collection("verses")
            .whereEqualTo("chapterId", chapterId)
            .orderBy("verseNumber", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val verses = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Verse::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(verses)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getVerseById(verseId: String): Verse? {
        return try {
            val doc = firestore.collection("verses").document(verseId).get().await()
            doc.toObject(Verse::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Full-text search across verses. Firestore doesn't support native full-text search,
     * so we search by tags array-contains or title prefix.
     * For production: consider Algolia free tier or Firebase Extensions.
     */
    fun searchVerses(query: String): Flow<List<Verse>> = callbackFlow {
        val lowerQuery = query.lowercase().trim()
        val listener = firestore.collection("verses")
            .whereArrayContains("tags", lowerQuery)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val verses = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Verse::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(verses)
            }
        awaitClose { listener.remove() }
    }

    // ─── Audio ────────────────────────────────────────────────────────────────

    fun getAllAudioItems(): Flow<List<AudioItem>> = callbackFlow {
        val listener = firestore.collection("audio")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(AudioItem::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    fun getAudioByReligion(religionId: String): Flow<List<AudioItem>> = callbackFlow {
        val listener = firestore.collection("audio")
            .whereEqualTo("religionId", religionId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(AudioItem::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    fun getAudioByCategory(category: AudioCategory): Flow<List<AudioItem>> = callbackFlow {
        val listener = firestore.collection("audio")
            .whereEqualTo("category", category.name)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(AudioItem::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    // ─── Daily Content ────────────────────────────────────────────────────────

    suspend fun getDailyContent(): DailyContent? {
        return try {
            val today = java.text.SimpleDateFormat(
                "yyyy-MM-dd", java.util.Locale.getDefault()
            ).format(java.util.Date())
            val doc = firestore.collection("daily_content").document(today).get().await()
            doc.toObject(DailyContent::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }
}
