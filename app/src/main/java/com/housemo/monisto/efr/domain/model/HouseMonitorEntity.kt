package com.housemo.monisto.efr.domain.model

import com.google.gson.annotations.SerializedName


data class HouseMonitorEntity (
    @SerializedName("ok")
    val houseMonitorOk: String,
    @SerializedName("url")
    val houseMonitorUrl: String,
    @SerializedName("expires")
    val houseMonitorExpires: Long,
)