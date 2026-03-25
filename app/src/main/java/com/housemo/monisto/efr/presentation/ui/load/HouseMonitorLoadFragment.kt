package com.housemo.monisto.efr.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.housemo.monisto.efr.data.shar.HouseMonitorSharedPreference
import com.housemo.monisto.MainActivity
import com.housemo.monisto.R
import com.housemo.monisto.databinding.FragmentLoadHouseMonitorBinding
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class HouseMonitorLoadFragment : Fragment(R.layout.fragment_load_house_monitor) {
    private lateinit var houseMonitorLoadBinding: FragmentLoadHouseMonitorBinding

    private val houseMonitorLoadViewModel by viewModel<HouseMonitorLoadViewModel>()

    private val houseMonitorSharedPreference by inject<HouseMonitorSharedPreference>()

    private var houseMonitorUrl = ""

    private val houseMonitorRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        houseMonitorSharedPreference.houseMonitorNotificationState = 2
        houseMonitorNavigateToSuccess(houseMonitorUrl)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        houseMonitorLoadBinding = FragmentLoadHouseMonitorBinding.bind(view)

        houseMonitorLoadBinding.houseMonitorGrandButton.setOnClickListener {
            val houseMonitorPermission = Manifest.permission.POST_NOTIFICATIONS
            houseMonitorRequestNotificationPermission.launch(houseMonitorPermission)
        }

        houseMonitorLoadBinding.houseMonitorSkipButton.setOnClickListener {
            houseMonitorSharedPreference.houseMonitorNotificationState = 1
            houseMonitorSharedPreference.houseMonitorNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            houseMonitorNavigateToSuccess(houseMonitorUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                houseMonitorLoadViewModel.houseMonitorHomeScreenState.collect {
                    when (it) {
                        is HouseMonitorLoadViewModel.HouseMonitorHomeScreenState.HouseMonitorLoading -> {

                        }

                        is HouseMonitorLoadViewModel.HouseMonitorHomeScreenState.HouseMonitorError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is HouseMonitorLoadViewModel.HouseMonitorHomeScreenState.HouseMonitorSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val houseMonitorNotificationState = houseMonitorSharedPreference.houseMonitorNotificationState
                                when (houseMonitorNotificationState) {
                                    0 -> {
                                        houseMonitorLoadBinding.houseMonitorNotiGroup.visibility = View.VISIBLE
                                        houseMonitorLoadBinding.houseMonitorLoadingGroup.visibility = View.GONE
                                        houseMonitorUrl = it.data
                                    }
                                    1 -> {
                                        if (System.currentTimeMillis() / 1000 > houseMonitorSharedPreference.houseMonitorNotificationRequest) {
                                            houseMonitorLoadBinding.houseMonitorNotiGroup.visibility = View.VISIBLE
                                            houseMonitorLoadBinding.houseMonitorLoadingGroup.visibility = View.GONE
                                            houseMonitorUrl = it.data
                                        } else {
                                            houseMonitorNavigateToSuccess(it.data)
                                        }
                                    }
                                    2 -> {
                                        houseMonitorNavigateToSuccess(it.data)
                                    }
                                }
                            } else {
                                houseMonitorNavigateToSuccess(it.data)
                            }
                        }

                        HouseMonitorLoadViewModel.HouseMonitorHomeScreenState.HouseMonitorNotInternet -> {
                            houseMonitorLoadBinding.houseMonitorStateGroup.visibility = View.VISIBLE
                            houseMonitorLoadBinding.houseMonitorLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun houseMonitorNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_houseMonitorLoadFragment_to_houseMonitorV,
            bundleOf(HOUSE_MONITOR_D to data)
        )
    }

    companion object {
        const val HOUSE_MONITOR_D = "houseMonitorData"
    }
}