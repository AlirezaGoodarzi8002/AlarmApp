package com.example.alarmapp.view.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alarmapp.view.state_holder.AlarmsViewModel
import com.example.alarmapp.databinding.ActivityAlarmsBinding
import com.example.alarmapp.util.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlarmsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmsBinding
    private val viewModel: AlarmsViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        PermissionUtil.grantAudioPermission(this)
        lifecycleScope.launch {
            delay(500)
            viewModel.initAlarms()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissionsToShowBottomSheet()
        binding.btnAdd.setOnClickListener { MainActivity.navigate(this) }
        observeAlarms()
    }

    private fun checkPermissionsToShowBottomSheet() {
        val needToGrantPermissions =
            !PermissionUtil.canDrawOverlays(this) ||
                    !PermissionUtil.isBatteryOptimizationIgnored(this)
        if (needToGrantPermissions)
            PermissionBottomSheetFragment.newInstance().show(supportFragmentManager, "")
    }

    private fun observeAlarms() {
        viewModel.alarms.observe(this) { alarms ->
            binding.rcAlarms.apply {
                layoutManager = LinearLayoutManager(
                    this@AlarmsActivity, LinearLayoutManager.VERTICAL, false
                )
                setHasFixedSize(true)
                adapter = AlarmsAdapter(alarms)
            }
        }
    }
}