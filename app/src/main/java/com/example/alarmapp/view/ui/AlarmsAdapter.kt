package com.example.alarmapp.view.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.alarmapp.MyApplication
import com.example.alarmapp.R
import com.example.alarmapp.databinding.ItemAlarmBinding
import com.example.alarmapp.view.state_holder.AlarmsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AlarmsAdapter(
    private val alarms: List<AlarmsViewModel.AlarmUiState>
) : Adapter<AlarmsAdapter.AlarmsViewHolder>() {

    private lateinit var context: Context

    class AlarmsViewHolder(val binding: ItemAlarmBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmsViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val binding = ItemAlarmBinding.inflate(inflater, parent, false)
        return AlarmsViewHolder(binding)
    }

    override fun getItemCount(): Int = alarms.size

    override fun onBindViewHolder(holder: AlarmsViewHolder, position: Int) {
        holder.binding.apply {
            val alarm = alarms[position]
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = alarm.timeMillis
            val dayOfWeek = getDaysOfWeekNames(calendar)
            val hour = calendar[Calendar.HOUR_OF_DAY]
            val minute = calendar[Calendar.MINUTE]
            val dateText = "$dayOfWeek  $hour:$minute"

            if (alarm.timeMillis <= System.currentTimeMillis() && !alarm.isRepeating)
                rootView.alpha = 0.3f

            tvAlarmTitle.text = alarm.title
            tvAlarmPlayTime.text =
                context.getString(R.string.play_time, alarm.alarmPlayTimeBySecond)
            tvAlarmTime.text = dateText

            ivDelete.setOnClickListener {
                alarm.onDelete(alarm.id)
                ivDelete.isClickable = false
            }
        }
    }

    private fun getDaysOfWeekNames(calendar: Calendar): String {
        val simpleDateFormat = SimpleDateFormat("EEEE", Locale("fa"))
        val date: Date = calendar.time
        return simpleDateFormat.format(date)
    }

}