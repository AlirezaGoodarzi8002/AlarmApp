package com.example.alarmapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.alarmapp.data.model.Alarm

@Dao
interface AlarmDao {

    @Insert
    suspend fun insert(alarm: Alarm): Long

    @Query("SELECT * FROM Alarm ORDER BY id DESC")
    suspend fun getAllAlarms(): List<Alarm>

    @Query("UPDATE Alarm SET title = :title, exact_time = :timeMillis, is_repeating" +
            " = :isRepeating, play_time = :playTime WHERE id = :alarmId")
    suspend fun updateById(alarmId: Long, title: String, timeMillis: Long, isRepeating: Boolean, playTime: Int)

    @Query("DELETE FROM Alarm WHERE id = :alarmId")
    suspend fun deleteById(alarmId: Long)
}