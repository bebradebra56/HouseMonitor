package com.housemo.monisto.efr.data.repo

import android.util.Log
import com.housemo.monisto.efr.domain.model.HouseMonitorEntity
import com.housemo.monisto.efr.domain.model.HouseMonitorParam
import com.housemo.monisto.efr.presentation.app.HouseMonitorApplication.Companion.HOUSE_MONITOR_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface HouseMonitorApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun houseMonitorGetClient(
        @Body jsonString: JsonObject,
    ): Call<HouseMonitorEntity>
}


private const val HOUSE_MONITOR_MAIN = "https://housemoniitor.com/"
class HouseMonitorRepository {

    suspend fun houseMonitorGetClient(
        houseMonitorParam: HouseMonitorParam,
        houseMonitorConversion: MutableMap<String, Any>?
    ): HouseMonitorEntity? {
        val gson = Gson()
        val api = houseMonitorGetApi(HOUSE_MONITOR_MAIN, null)

        val houseMonitorJsonObject = gson.toJsonTree(houseMonitorParam).asJsonObject
        houseMonitorConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            houseMonitorJsonObject.add(key, element)
        }
        return try {
            val houseMonitorRequest: Call<HouseMonitorEntity> = api.houseMonitorGetClient(
                jsonString = houseMonitorJsonObject,
            )
            val houseMonitorResult = houseMonitorRequest.awaitResponse()
            Log.d(HOUSE_MONITOR_MAIN_TAG, "Retrofit: Result code: ${houseMonitorResult.code()}")
            if (houseMonitorResult.code() == 200) {
                Log.d(HOUSE_MONITOR_MAIN_TAG, "Retrofit: Get request success")
                Log.d(HOUSE_MONITOR_MAIN_TAG, "Retrofit: Code = ${houseMonitorResult.code()}")
                Log.d(HOUSE_MONITOR_MAIN_TAG, "Retrofit: ${houseMonitorResult.body()}")
                houseMonitorResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(HOUSE_MONITOR_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(HOUSE_MONITOR_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun houseMonitorGetApi(url: String, client: OkHttpClient?) : HouseMonitorApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
