package com.housemo.monisto.efr.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.housemo.monisto.efr.presentation.app.HouseMonitorApplication

class HouseMonitorPushHandler {
    fun houseMonitorHandlePush(extras: Bundle?) {
        Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = houseMonitorBundleToMap(extras)
            Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    HouseMonitorApplication.HOUSE_MONITOR_FB_LI = map["url"]
                    Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "Push data no!")
        }
    }

    private fun houseMonitorBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}