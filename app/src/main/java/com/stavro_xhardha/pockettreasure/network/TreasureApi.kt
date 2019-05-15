package com.stavro_xhardha.pockettreasure.network

import com.stavro_xhardha.pockettreasure.model.Country
import com.stavro_xhardha.pockettreasure.model.NameResponse
import com.stavro_xhardha.pockettreasure.model.PrayerTimeResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface TreasureApi {

    @GET("asmaAlHusna")
    fun getNintyNineNamesAsync(): Deferred<Response<NameResponse>>

    @GET
    fun getCountriesList(@Url url: String): Deferred<Response<ArrayList<Country>>>

    @GET("timingsByCity")
    fun getPrayerTimesTodayAsync(@Query("city") city: String?, @Query("country") country: String?):
            Deferred<Response<PrayerTimeResponse>>
}