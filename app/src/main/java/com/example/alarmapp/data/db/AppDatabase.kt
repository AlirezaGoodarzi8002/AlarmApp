package com.example.alarmapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.alarmapp.data.model.Alarm

@Database(entities = [Alarm::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
}