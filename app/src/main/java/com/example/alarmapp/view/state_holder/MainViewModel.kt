package com.example.alarmapp.view.state_holder

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarmapp.data.repo.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

private const val WEEK_DAYS_COUNT = 7

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AlarmRepository, private val application: Application
) : AndroidViewModel(application) {

    private lateinit var alarmManager: AlarmManager

    init {
        if (!::alarmManager.isInitialized)
            alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun setRepeatingAlarm(
        calendar: Calendar, intent: Intent, title: String, isRepeating: Boolean, playtime: Int
    ) {
        viewModelScope.launch {
            val alarmId =
                repository.insertAlarm(title, calendar.timeInMillis, isRepeating, playtime).toInt()
            val pendingIntent = PendingIntent.getBroadcast(
                application.applicationContext, alarmId, intent, PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY * WEEK_DAYS_COUNT, pendingIntent
            )
        }
    }

    fun setAlarmClock(
        calendar: Calendar, intent: Intent, title: String, isRepeating: Boolean, playtime: Int
    ) {
        viewModelScope.launch {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms())
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.S
            ) {
                val alarmId =
                    repository.insertAlarm(title, calendar.timeInMillis, isRepeating, playtime)
                        .toInt()
                val pendingIntent = PendingIntent.getBroadcast(
                    application.applicationContext,
                    alarmId,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )

                if (isRepeating)
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY * WEEK_DAYS_COUNT, pendingIntent
                    )
                else
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
                        )
                    else
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
                        )
            }
        }
    }
}