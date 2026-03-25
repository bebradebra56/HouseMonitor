package com.housemo.monisto.efr.domain.model

import com.google.gson.annotations.SerializedName


private const val HOUSE_MONITOR_A = "com.housemo.monisto"
private const val HOUSE_MONITOR_B = "housemonitor-f7fa2"
data class HouseMonitorParam (
    @SerializedName("af_id")
    val houseMonitorAfId: String,
    @SerializedName("bundle_id")
    val houseMonitorBundleId: String = HOUSE_MONITOR_A,
    @SerializedName("os")
    val houseMonitorOs: String = "Android",
    @SerializedName("store_id")
    val houseMonitorStoreId: String = HOUSE_MONITOR_A,
    @SerializedName("locale")
    val houseMonitorLocale: String,
    @SerializedName("push_token")
    val houseMonitorPushToken: String,
    @SerializedName("firebase_project_id")
    val houseMonitorFirebaseProjectId: String = HOUSE_MONITOR_B,

    )