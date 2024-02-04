package com.example.alarmapp.component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.alarmapp.view.ui.MainActivity.Companion.DEFAULT_PLAY_TIME
import com.example.alarmapp.view.ui.MainActivity.Companion.FILE_URI_KEY
import com.example.alarmapp.view.ui.MainActivity.Companion.PLAY_TIME_KEY
import com.example.alarmapp.view.ui.MainActivity.Companion.TITLE_KEY

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.extras?.getString(TITLE_KEY) ?: ""
        val playTime = intent?.extras?.getInt(PLAY_TIME_KEY) ?: DEFAULT_PLAY_TIME
        val soundUri = intent?.extras?.getString(FILE_URI_KEY)
        val mediaIntent = Intent(context, MediaService::class.java).apply {
            putExtra(TITLE_KEY, title)
            putExtra(PLAY_TIME_KEY, playTime)
            putExtra(FILE_URI_KEY, soundUri)  }
        context?.startService(mediaIntent)
    }
}