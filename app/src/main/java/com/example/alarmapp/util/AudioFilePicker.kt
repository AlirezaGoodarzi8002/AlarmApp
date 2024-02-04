package com.example.alarmapp.util

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.example.alarmapp.R

object AudioFilePicker {

    const val PICK_SOUND_REQUEST_CODE = 1200

    fun pickFile(activity: Activity) {
        try {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "audio/*"
            activity.startActivityForResult(intent, PICK_SOUND_REQUEST_CODE)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(activity,
                activity.getString(R.string.please_grant_permission_first), Toast.LENGTH_SHORT
            ).show()
            PermissionUtil.grantAudioPermission(activity)
        }
    }

}