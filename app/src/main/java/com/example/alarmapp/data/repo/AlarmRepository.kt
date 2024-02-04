package com.example.alarmapp.data.repo

import com.example.alarmapp.data.model.Alarm
import com.example.alarmapp.data.db.AlarmDao
import javax.inject.Inject

class AlarmRepository @Inject constructor(private val alarmDao: AlarmDao) {

    suspend fun insertAlarm(
        title: String, timeMillis: Long, isRepeating: Boolean, playTime: Int
    ): Long = alarmDao.insert(
        Alarm(title, timeMillis, isRepeating, playTime)
    )

    suspend fun getAllAlarms() = alarmDao.getAllAlarms()

    suspend fun updateAlarmById(
        id: Long, title: String, timeMillis: Long, isRepeating: Boolean, playTime: Int
    ) = alarmDao.updateById(id, title, timeMillis, isRepeating, playTime)

    suspend fun deleteAlarmById(id: Long) = alarmDao.deleteById(id)
}