package com.example.alarmapp.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.alarmapp.databinding.BottomSheetPermissionBinding
import com.example.alarmapp.util.PermissionUtil
import com.example.alarmapp.view.state_holder.PermissionViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance() = PermissionBottomSheetFragment()
    }

    private val viewModel: PermissionViewModel by viewModels()
    private var _binding: BottomSheetPermissionBinding? = null
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        if (PermissionUtil.canDrawOverlays(requireActivity())) {
            binding.checkboxDisplayOverApps.apply {
                isClickable = false
                isFocusable = false
                isChecked = true
            }
        }
        if (PermissionUtil.isBatteryOptimizationIgnored(requireActivity())) {
            binding.checkboxBatteryOptimization.apply {
                isClickable = false
                isFocusable = false
                isChecked = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawOverlayIfNot()
        ignoreBatteryOptimizationIfNot()
    }

    private fun drawOverlayIfNot() {
        if (!PermissionUtil.canDrawOverlays(requireActivity())) {
            binding.checkboxDisplayOverApps.setOnClickListener {
                binding.checkboxDisplayOverApps.isChecked = false
                PermissionUtil.grantDisplayOverApps(requireActivity())
            }
        } else {
            binding.checkboxDisplayOverApps.apply {
                isClickable = false
                isChecked = true
            }
        }
    }

    private fun ignoreBatteryOptimizationIfNot() {
        if (!PermissionUtil.isBatteryOptimizationIgnored(requireActivity())) {
            binding.checkboxBatteryOptimization.setOnClickListener {
                binding.checkboxBatteryOptimization.isChecked = false
                PermissionUtil.grantBatteryOptimization(requireActivity())
            }
        } else {
            binding.checkboxBatteryOptimization.apply {
                isClickable = false
                isChecked = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}