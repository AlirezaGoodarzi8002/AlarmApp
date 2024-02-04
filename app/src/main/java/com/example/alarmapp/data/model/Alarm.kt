package com.example.alarmapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Alarm (
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "exact_time") val exactTimeMillis: Long,
    @ColumnInfo(name = "is_repeating") val isRepeating: Boolean,
    @ColumnInfo(name = "play_time") val playTime: Int,
) {
    @PrimaryKey(true)
    var id: Long = 0
}