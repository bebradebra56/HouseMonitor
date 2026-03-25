package com.housemo.monisto.efr.data.utils

import android.util.Log
import com.housemo.monisto.efr.presentation.app.HouseMonitorApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class HouseMonitorPushToken {

    suspend fun houseMonitorGetToken(
        houseMonitorMaxAttempts: Int = 3,
        houseMonitorDelayMs: Long = 1500
    ): String {

        repeat(houseMonitorMaxAttempts - 1) {
            try {
                val houseMonitorToken = FirebaseMessaging.getInstance().token.await()
                return houseMonitorToken
            } catch (e: Exception) {
                Log.e(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(houseMonitorDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}