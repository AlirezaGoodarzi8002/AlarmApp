package com.example.alarmapp.view.state_holder

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarmapp.component.AlarmReceiver
import com.example.alarmapp.data.repo.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmsViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository, private val application: Application
): AndroidViewModel(application) {

    private lateinit var alarmManager: AlarmManager
    private val _alarms = MutableLiveData<List<AlarmUiState>>()
    val alarms: LiveData<List<AlarmUiState>> get() = _alarms

    init {
        if (!::alarmManager.isInitialized)
            alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun initAlarms() {
        viewModelScope.launch {
            val alarms = alarmRepository.getAllAlarms()
            if (alarms.isNotEmpty()) {
                _alarms.value = alarms.map { alarm ->
                    AlarmUiState(
                        alarm.id, alarm.title, alarm.exactTimeMillis, alarm.isRepeating,
                        alarm.playTime) { alarmId ->
                        val intent = Intent(application, AlarmReceiver::class.java)
                        val pendingIntent = PendingIntent.getBroadcast(
                            application.applicationContext,
                            alarmId.toInt(), intent, PendingIntent.FLAG_IMMUTABLE
                        )
                        alarmManager.cancel(pendingIntent)
                        viewModelScope.launch {
                            alarmRepository.deleteAlarmById(alarmId)
                            delay(500)
                            val removedAlarm = _alarms.value?.first { it.id == alarm.id }
                            val newList = _alarms.value?.toMutableList()
                            newList?.apply {
                                remove(removedAlarm)
                                _alarms.value = this
                            }
                        }
                    }
                }
            }
        }
    }

    data class AlarmUiState(
        val id: Long,
        val title: String,
        val timeMillis: Long,
        val isRepeating: Boolean,
        val alarmPlayTimeBySecond: Int,
        val onDelete: (Long) -> Unit
    )
}