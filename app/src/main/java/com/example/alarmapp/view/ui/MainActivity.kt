package com.example.alarmapp.view.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.alarmapp.R
import com.example.alarmapp.component.AlarmReceiver
import com.example.alarmapp.databinding.ActivityMainBinding
import com.example.alarmapp.util.AudioFilePicker
import com.example.alarmapp.util.AudioFilePicker.PICK_SOUND_REQUEST_CODE
import com.example.alarmapp.view.state_holder.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val FILE_URI_KEY = "fileUri"
        const val FILE_PATH_KEY = "filePath"
        const val PLAY_TIME_KEY = "playTime"
        const val TITLE_KEY = "title"
        const val DEFAULT_PLAY_TIME = 3

        fun navigate(context: Context) =
            context.startActivity(Intent(context, MainActivity::class.java))
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var calendar: Calendar
    private val viewModel: MainViewModel by viewModels()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            var playtime = DEFAULT_PLAY_TIME
            playTimeTextView.text = getString(R.string.play_time, playtime)
            setAlarmButton.setOnClickListener { showTimePickerDialog() }
            setDatePickerAvailablity()
            btnAdd.setOnClickListener {
                playtime++
                playTimeTextView.text = getString(R.string.play_time, playtime)
            }
            btnSubtract.setOnClickListener {
                if (playtime < DEFAULT_PLAY_TIME)
                    return@setOnClickListener
                playtime--
                playTimeTextView.text = getString(R.string.play_time, playtime)
            }
        }
    }

    private fun ActivityMainBinding.setDatePickerAvailablity() {
        mondayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            setDatePickerDisabled(isChecked)
        }
        tuesdayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            setDatePickerDisabled(isChecked)
        }
        wednesdayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            setDatePickerDisabled(isChecked)
        }
        thursdayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            setDatePickerDisabled(isChecked)
        }
        fridayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            setDatePickerDisabled(isChecked)
        }
        saturdayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            setDatePickerDisabled(isChecked)
        }
        sundayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            setDatePickerDisabled(isChecked)
        }
        repeatSwitch.setOnCheckedChangeListener { _, isChecked ->
            setDatePickerDisabled(isChecked)
        }
    }

    private fun setDatePickerDisabled(isChecked: Boolean) {
        binding.lockDatePickerView.apply {
            isVisible = isChecked
            setOnClickListener { }
        }
        binding.datePicker.alpha = if (isChecked) 0.3f else 1f
    }

    private fun showTimePickerDialog() {
        if (!binding.repeatSwitch.isChecked && getSelectedDays().isNotEmpty()) {
            calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, binding.datePicker.year)
            calendar.set(Calendar.MONTH, binding.datePicker.month)
            calendar.set(Calendar.DAY_OF_MONTH, binding.datePicker.dayOfMonth)
        } else calendar = Calendar.getInstance()

        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = getTimePickerDialog(currentHour, currentMinute)
        timePickerDialog.show()
    }

    private fun getTimePickerDialog(
        currentHour: Int,
        currentMinute: Int
    ) = TimePickerDialog(
        this, { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)

            AudioFilePicker.pickFile(this)
        }, currentHour, currentMinute, true
    )

    private fun getSelectedDays(): List<Int> {
        val days = mutableListOf<Int>()
        binding.apply {
            if (sundayCheckBox.isChecked) days.add(Calendar.SUNDAY)
            if (mondayCheckBox.isChecked) days.add(Calendar.MONDAY)
            if (tuesdayCheckBox.isChecked) days.add(Calendar.TUESDAY)
            if (wednesdayCheckBox.isChecked) days.add(Calendar.WEDNESDAY)
            if (thursdayCheckBox.isChecked) days.add(Calendar.THURSDAY)
            if (fridayCheckBox.isChecked) days.add(Calendar.FRIDAY)
            if (saturdayCheckBox.isChecked) days.add(Calendar.SATURDAY)
        }
        return days
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (PICK_SOUND_REQUEST_CODE == requestCode && resultCode == Activity.RESULT_OK) {
            contentResolver.takePersistableUriPermission(data?.data!!, FLAG_GRANT_READ_URI_PERMISSION)
            val intent = Intent(this, AlarmReceiver::class.java).apply {
                putExtra(TITLE_KEY, binding.titleEditText.text)
                putExtra(PLAY_TIME_KEY, binding.playTimeTextView.text.toString().first().digitToInt())
                putExtra(FILE_URI_KEY, data?.data.toString())
            }

            val days = getSelectedDays()
            if (days.isEmpty()) viewModel.setAlarmClock(
                calendar, intent,
                binding.titleEditText.text?.toString() ?: "آلارم",
                binding.repeatSwitch.isChecked,
                binding.playTimeTextView.text.toString().first().digitToInt()
            )
            else setMultipleAlarm(days, intent)
            super.onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setMultipleAlarm(days: List<Int>, intent: Intent) {
        binding.apply {
            for (dayOfWeek in days) {
                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)

                if (!binding.repeatSwitch.isChecked)
                    viewModel.setAlarmClock(
                        calendar, intent, titleEditText.text.toString(),
                        repeatSwitch.isChecked, playTimeTextView.text.toString().first().digitToInt()
                    )
                else
                    viewModel.setRepeatingAlarm(
                        calendar, intent, titleEditText.text.toString(),
                        repeatSwitch.isChecked, playTimeTextView.text.toString().first().digitToInt()
                    )
            }
        }
    }
}