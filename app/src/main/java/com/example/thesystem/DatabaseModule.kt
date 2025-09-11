package com.example.thesystem

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.getDatabase(appContext)
    }

    @Provides
    fun provideQuestDao(appDatabase: AppDatabase): QuestDao {
        return appDatabase.questDao()
    }

    @Provides
    fun provideUserStatsDao(appDatabase: AppDatabase): UserStatsDao {
        return appDatabase.userStatsDao()
    }
}