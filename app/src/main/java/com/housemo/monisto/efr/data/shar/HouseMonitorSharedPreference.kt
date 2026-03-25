package com.housemo.monisto.efr.data.shar

import android.content.Context
import androidx.core.content.edit

class HouseMonitorSharedPreference(context: Context) {
    private val houseMonitorPrefs = context.getSharedPreferences("houseMonitorSharedPrefsAb", Context.MODE_PRIVATE)

    var houseMonitorSavedUrl: String
        get() = houseMonitorPrefs.getString(HOUSE_MONITOR_SAVED_URL, "") ?: ""
        set(value) = houseMonitorPrefs.edit { putString(HOUSE_MONITOR_SAVED_URL, value) }

    var houseMonitorExpired : Long
        get() = houseMonitorPrefs.getLong(HOUSE_MONITOR_EXPIRED, 0L)
        set(value) = houseMonitorPrefs.edit { putLong(HOUSE_MONITOR_EXPIRED, value) }

    var houseMonitorAppState: Int
        get() = houseMonitorPrefs.getInt(HOUSE_MONITOR_APPLICATION_STATE, 0)
        set(value) = houseMonitorPrefs.edit { putInt(HOUSE_MONITOR_APPLICATION_STATE, value) }

    var houseMonitorNotificationRequest: Long
        get() = houseMonitorPrefs.getLong(HOUSE_MONITOR_NOTIFICAITON_REQUEST, 0L)
        set(value) = houseMonitorPrefs.edit { putLong(HOUSE_MONITOR_NOTIFICAITON_REQUEST, value) }


    var houseMonitorNotificationState:Int
        get() = houseMonitorPrefs.getInt(HOUSE_MONITOR_NOTIFICATION_STATE, 0)
        set(value) = houseMonitorPrefs.edit { putInt(HOUSE_MONITOR_NOTIFICATION_STATE, value) }

    companion object {
        private const val HOUSE_MONITOR_NOTIFICATION_STATE = "houseMonitorNotificationState"
        private const val HOUSE_MONITOR_SAVED_URL = "houseMonitorSavedUrl"
        private const val HOUSE_MONITOR_EXPIRED = "houseMonitorExpired"
        private const val HOUSE_MONITOR_APPLICATION_STATE = "houseMonitorApplicationState"
        private const val HOUSE_MONITOR_NOTIFICAITON_REQUEST = "houseMonitorNotificationRequest"
    }
}