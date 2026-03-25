package com.housemo.monisto.efr.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housemo.monisto.efr.data.shar.HouseMonitorSharedPreference
import com.housemo.monisto.efr.data.utils.HouseMonitorSystemService
import com.housemo.monisto.efr.domain.usecases.HouseMonitorGetAllUseCase
import com.housemo.monisto.efr.presentation.app.HouseMonitorAppsFlyerState
import com.housemo.monisto.efr.presentation.app.HouseMonitorApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HouseMonitorLoadViewModel(
    private val houseMonitorGetAllUseCase: HouseMonitorGetAllUseCase,
    private val houseMonitorSharedPreference: HouseMonitorSharedPreference,
    private val houseMonitorSystemService: HouseMonitorSystemService
) : ViewModel() {

    private val _houseMonitorHomeScreenState: MutableStateFlow<HouseMonitorHomeScreenState> =
        MutableStateFlow(HouseMonitorHomeScreenState.HouseMonitorLoading)
    val houseMonitorHomeScreenState = _houseMonitorHomeScreenState.asStateFlow()

    private var houseMonitorGetApps = false


    init {
        viewModelScope.launch {
            when (houseMonitorSharedPreference.houseMonitorAppState) {
                0 -> {
                    if (houseMonitorSystemService.houseMonitorIsOnline()) {
                        HouseMonitorApplication.houseMonitorConversionFlow.collect {
                            when(it) {
                                HouseMonitorAppsFlyerState.HouseMonitorDefault -> {}
                                HouseMonitorAppsFlyerState.HouseMonitorError -> {
                                    houseMonitorSharedPreference.houseMonitorAppState = 2
                                    _houseMonitorHomeScreenState.value =
                                        HouseMonitorHomeScreenState.HouseMonitorError
                                    houseMonitorGetApps = true
                                }
                                is HouseMonitorAppsFlyerState.HouseMonitorSuccess -> {
                                    if (!houseMonitorGetApps) {
                                        houseMonitorGetData(it.houseMonitorData)
                                        houseMonitorGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _houseMonitorHomeScreenState.value =
                            HouseMonitorHomeScreenState.HouseMonitorNotInternet
                    }
                }
                1 -> {
                    if (houseMonitorSystemService.houseMonitorIsOnline()) {
                        if (HouseMonitorApplication.HOUSE_MONITOR_FB_LI != null) {
                            _houseMonitorHomeScreenState.value =
                                HouseMonitorHomeScreenState.HouseMonitorSuccess(
                                    HouseMonitorApplication.HOUSE_MONITOR_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > houseMonitorSharedPreference.houseMonitorExpired) {
                            Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "Current time more then expired, repeat request")
                            HouseMonitorApplication.houseMonitorConversionFlow.collect {
                                when(it) {
                                    HouseMonitorAppsFlyerState.HouseMonitorDefault -> {}
                                    HouseMonitorAppsFlyerState.HouseMonitorError -> {
                                        _houseMonitorHomeScreenState.value =
                                            HouseMonitorHomeScreenState.HouseMonitorSuccess(
                                                houseMonitorSharedPreference.houseMonitorSavedUrl
                                            )
                                        houseMonitorGetApps = true
                                    }
                                    is HouseMonitorAppsFlyerState.HouseMonitorSuccess -> {
                                        if (!houseMonitorGetApps) {
                                            houseMonitorGetData(it.houseMonitorData)
                                            houseMonitorGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "Current time less then expired, use saved url")
                            _houseMonitorHomeScreenState.value =
                                HouseMonitorHomeScreenState.HouseMonitorSuccess(
                                    houseMonitorSharedPreference.houseMonitorSavedUrl
                                )
                        }
                    } else {
                        _houseMonitorHomeScreenState.value =
                            HouseMonitorHomeScreenState.HouseMonitorNotInternet
                    }
                }
                2 -> {
                    _houseMonitorHomeScreenState.value =
                        HouseMonitorHomeScreenState.HouseMonitorError
                }
            }
        }
    }


    private suspend fun houseMonitorGetData(conversation: MutableMap<String, Any>?) {
        val houseMonitorData = houseMonitorGetAllUseCase.invoke(conversation)
        if (houseMonitorSharedPreference.houseMonitorAppState == 0) {
            if (houseMonitorData == null) {
                houseMonitorSharedPreference.houseMonitorAppState = 2
                _houseMonitorHomeScreenState.value =
                    HouseMonitorHomeScreenState.HouseMonitorError
            } else {
                houseMonitorSharedPreference.houseMonitorAppState = 1
                houseMonitorSharedPreference.apply {
                    houseMonitorExpired = houseMonitorData.houseMonitorExpires
                    houseMonitorSavedUrl = houseMonitorData.houseMonitorUrl
                }
                _houseMonitorHomeScreenState.value =
                    HouseMonitorHomeScreenState.HouseMonitorSuccess(houseMonitorData.houseMonitorUrl)
            }
        } else  {
            if (houseMonitorData == null) {
                _houseMonitorHomeScreenState.value =
                    HouseMonitorHomeScreenState.HouseMonitorSuccess(
                        houseMonitorSharedPreference.houseMonitorSavedUrl
                    )
            } else {
                houseMonitorSharedPreference.apply {
                    houseMonitorExpired = houseMonitorData.houseMonitorExpires
                    houseMonitorSavedUrl = houseMonitorData.houseMonitorUrl
                }
                _houseMonitorHomeScreenState.value =
                    HouseMonitorHomeScreenState.HouseMonitorSuccess(houseMonitorData.houseMonitorUrl)
            }
        }
    }


    sealed class HouseMonitorHomeScreenState {
        data object HouseMonitorLoading : HouseMonitorHomeScreenState()
        data object HouseMonitorError : HouseMonitorHomeScreenState()
        data class HouseMonitorSuccess(val data: String) : HouseMonitorHomeScreenState()
        data object HouseMonitorNotInternet: HouseMonitorHomeScreenState()
    }
}