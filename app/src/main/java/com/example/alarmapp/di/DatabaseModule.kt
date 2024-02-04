package com.example.alarmapp.di

import android.content.Context
import androidx.room.Room
import com.example.alarmapp.data.db.AlarmDao
import com.example.alarmapp.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = Room
        .databaseBuilder(context, AppDatabase::class.java, "database-name").build()

    @Provides
    fun provideAlarmDao(appDatabase: AppDatabase): AlarmDao = appDatabase.alarmDao()

}