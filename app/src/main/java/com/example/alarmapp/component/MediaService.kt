package com.example.alarmapp.component

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.alarmapp.R
import com.example.alarmapp.view.ui.AlarmsActivity
import com.example.alarmapp.view.ui.MainActivity
import com.example.alarmapp.view.ui.MainActivity.Companion.TITLE_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class MediaService : Service() {

    companion object {
        private const val CHANNEL_ID = "alarm_app_channel"
        const val NOTIFICATION_ID = 1
    }

    private var mediaPlayer: MediaPlayer? = null
    private var title: String? = null
    private var playTime by Delegates.notNull<Int>()
    private var soundUri: Uri? = null
    private var isAlarmRepeating: Boolean = false
    private var notificationManager: NotificationManager? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification(this))
        title = intent?.extras?.getString(TITLE_KEY) ?: ""
        playTime = intent?.extras?.getInt(MainActivity.PLAY_TIME_KEY) ?: MainActivity.DEFAULT_PLAY_TIME
        soundUri = Uri.parse(intent?.extras?.getString(MainActivity.FILE_URI_KEY))
        isAlarmRepeating = intent?.extras?.getBoolean(MainActivity.IS_ALARM_REPEATING_KEY) ?: false
        mediaPlayer = MediaPlayer.create(applicationContext, soundUri)
        playSound()

        return START_STICKY
    }

    private fun playSound() {
        if (mediaPlayer?.isPlaying != true) {
            mediaPlayer?.start()
            CoroutineScope(Dispatchers.Main).launch {
                val secondInterval = 1000L
                println("stopping...")
                delay(playTime * secondInterval)
                println("service stopped")
                stopService()
            }
        }
    }

    private fun stopService() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        notificationManager?.cancel(NOTIFICATION_ID)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(context: Context): Notification {
        val notificationIntent = Intent(context, AlarmsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm App",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title?.ifEmpty { "آلارم" } ?: "آلارم")
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()
    }
}