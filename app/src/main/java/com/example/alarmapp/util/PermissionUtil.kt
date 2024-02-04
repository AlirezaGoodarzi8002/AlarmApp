package com.example.alarmapp.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtil {

    private const val REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 1000

    fun grantAudioPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) grantFromHigherThan32(activity)
        else grantFromLowerThan32(activity)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun grantFromHigherThan32(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                REQUEST_READ_EXTERNAL_STORAGE_PERMISSION
            )
        }
    }

    private fun grantFromLowerThan32(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_EXTERNAL_STORAGE_PERMISSION
            )
        }
    }

    fun grantBatteryOptimization(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                activity.startActivity(
                    Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun isBatteryOptimizationIgnored(activity: Activity): Boolean {
        val powerManager = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                powerManager.isIgnoringBatteryOptimizations(activity.packageName)
            else true
    }

    fun grantDisplayOverApps(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val overlayPermissionIntent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + activity.packageName)
            )
            activity.startActivity(overlayPermissionIntent)
        }
    }

    fun canDrawOverlays(context: Context?): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            Settings.canDrawOverlays(context)
        else true
    }

}