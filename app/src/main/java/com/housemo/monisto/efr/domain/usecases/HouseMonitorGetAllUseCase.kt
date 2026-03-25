package com.housemo.monisto.efr.domain.usecases

import android.util.Log
import com.housemo.monisto.efr.data.repo.HouseMonitorRepository
import com.housemo.monisto.efr.data.utils.HouseMonitorPushToken
import com.housemo.monisto.efr.data.utils.HouseMonitorSystemService
import com.housemo.monisto.efr.domain.model.HouseMonitorEntity
import com.housemo.monisto.efr.domain.model.HouseMonitorParam
import com.housemo.monisto.efr.presentation.app.HouseMonitorApplication

class HouseMonitorGetAllUseCase(
    private val houseMonitorRepository: HouseMonitorRepository,
    private val houseMonitorSystemService: HouseMonitorSystemService,
    private val houseMonitorPushToken: HouseMonitorPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : HouseMonitorEntity?{
        val params = HouseMonitorParam(
            houseMonitorLocale = houseMonitorSystemService.houseMonitorGetLocale(),
            houseMonitorPushToken = houseMonitorPushToken.houseMonitorGetToken(),
            houseMonitorAfId = houseMonitorSystemService.houseMonitorGetAppsflyerId()
        )
        Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "Params for request: $params")
        return houseMonitorRepository.houseMonitorGetClient(params, conversion)
    }



}