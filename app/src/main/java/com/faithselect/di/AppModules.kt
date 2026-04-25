package com.faithselect.di

import android.content.Context
import androidx.room.Room
import com.faithselect.data.krishna.FreemiumService
import com.faithselect.data.krishna.KrishnaAIService
import com.faithselect.data.local.FaithSelectDatabase
import com.faithselect.data.local.dao.FavoriteAudioDao
import com.faithselect.data.local.dao.FavoriteVerseDao
import com.faithselect.data.repository.*
import com.faithselect.domain.repository.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FaithSelectDatabase =
        Room.databaseBuilder(context, FaithSelectDatabase::class.java, "faith_select_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideFavoriteVerseDao(db: FaithSelectDatabase): FavoriteVerseDao = db.favoriteVerseDao()

    @Provides
    fun provideFavoriteAudioDao(db: FaithSelectDatabase): FavoriteAudioDao = db.favoriteAudioDao()
}

@Module
@InstallIn(SingletonComponent::class)
object KrishnaModule {
    @Provides
    @Singleton
    fun provideKrishnaAIService(@ApplicationContext context: Context): KrishnaAIService =
        KrishnaAIService(context)

    @Provides
    @Singleton
    fun provideFreemiumService(@ApplicationContext context: Context): FreemiumService =
        FreemiumService(context)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindContentRepository(impl: ContentRepositoryImpl): ContentRepository

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(impl: FavoritesRepositoryImpl): FavoritesRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(impl: PreferencesRepositoryImpl): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(impl: SubscriptionRepositoryImpl): SubscriptionRepository
}