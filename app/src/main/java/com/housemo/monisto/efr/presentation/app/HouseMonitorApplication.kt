package com.housemo.monisto.efr.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.housemo.monisto.di.databaseModule
import com.housemo.monisto.di.repositoryModule
import com.housemo.monisto.di.viewModelModule
import com.housemo.monisto.efr.presentation.di.houseMonitorModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface HouseMonitorAppsFlyerState {
    data object HouseMonitorDefault : HouseMonitorAppsFlyerState
    data class HouseMonitorSuccess(val houseMonitorData: MutableMap<String, Any>?) :
        HouseMonitorAppsFlyerState

    data object HouseMonitorError : HouseMonitorAppsFlyerState
}

interface HouseMonitorAppsApi {
    @Headers("Content-Type: application/json")
    @GET(HOUSE_MONITOR_LIN)
    fun houseMonitorGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val HOUSE_MONITOR_APP_DEV = "876jQR9fEn4qUVmMpXqMF7"
private const val HOUSE_MONITOR_LIN = "com.housemo.monisto"

class HouseMonitorApplication : Application() {


    private var houseMonitorIsResumed = false
    ///////
    private var houseMonitorConversionTimeoutJob: Job? = null
    private var houseMonitorDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        houseMonitorSetDebufLogger(appsflyer)
        houseMonitorMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        houseMonitorExtractDeepMap(p0.deepLink)
                        Log.d(HOUSE_MONITOR_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(HOUSE_MONITOR_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(HOUSE_MONITOR_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            HOUSE_MONITOR_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    //////////
                    houseMonitorConversionTimeoutJob?.cancel()
                    Log.d(HOUSE_MONITOR_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = houseMonitorGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.houseMonitorGetClient(
                                    devkey = HOUSE_MONITOR_APP_DEV,
                                    deviceId = houseMonitorGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(HOUSE_MONITOR_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    houseMonitorResume(
                                        HouseMonitorAppsFlyerState.HouseMonitorError
                                    )
                                } else {
                                    houseMonitorResume(
                                        HouseMonitorAppsFlyerState.HouseMonitorSuccess(
                                            resp
                                        )
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(HOUSE_MONITOR_MAIN_TAG, "Error: ${d.message}")
                                houseMonitorResume(HouseMonitorAppsFlyerState.HouseMonitorError)
                            }
                        }
                    } else {
                        houseMonitorResume(
                            HouseMonitorAppsFlyerState.HouseMonitorSuccess(
                                p0
                            )
                        )
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    /////////
                    houseMonitorConversionTimeoutJob?.cancel()
                    Log.d(HOUSE_MONITOR_MAIN_TAG, "onConversionDataFail: $p0")
                    houseMonitorResume(HouseMonitorAppsFlyerState.HouseMonitorError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(HOUSE_MONITOR_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(HOUSE_MONITOR_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, HOUSE_MONITOR_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(HOUSE_MONITOR_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(HOUSE_MONITOR_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
        ///////////
        houseMonitorStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@HouseMonitorApplication)
            modules(
                listOf(
                    houseMonitorModule, databaseModule, repositoryModule, viewModelModule
                )
            )
        }
    }

    private fun houseMonitorExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(HOUSE_MONITOR_MAIN_TAG, "Extracted DeepLink data: $map")
        houseMonitorDeepLinkData = map
    }
    /////////////////

    private fun houseMonitorStartConversionTimeout() {
        houseMonitorConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!houseMonitorIsResumed) {
                Log.d(HOUSE_MONITOR_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                houseMonitorResume(HouseMonitorAppsFlyerState.HouseMonitorError)
            }
        }
    }

    private fun houseMonitorResume(state: HouseMonitorAppsFlyerState) {
        ////////////
        houseMonitorConversionTimeoutJob?.cancel()
        if (state is HouseMonitorAppsFlyerState.HouseMonitorSuccess) {
            val convData = state.houseMonitorData ?: mutableMapOf()
            val deepData = houseMonitorDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!houseMonitorIsResumed) {
                houseMonitorIsResumed = true
                houseMonitorConversionFlow.value =
                    HouseMonitorAppsFlyerState.HouseMonitorSuccess(merged)
            }
        } else {
            if (!houseMonitorIsResumed) {
                houseMonitorIsResumed = true
                houseMonitorConversionFlow.value = state
            }
        }
    }

    private fun houseMonitorGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(HOUSE_MONITOR_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun houseMonitorSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun houseMonitorMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun houseMonitorGetApi(url: String, client: OkHttpClient?): HouseMonitorAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {
        var houseMonitorInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val houseMonitorConversionFlow: MutableStateFlow<HouseMonitorAppsFlyerState> = MutableStateFlow(
            HouseMonitorAppsFlyerState.HouseMonitorDefault
        )
        var HOUSE_MONITOR_FB_LI: String? = null
        const val HOUSE_MONITOR_MAIN_TAG = "HouseMonitorMainTag"
    }
}