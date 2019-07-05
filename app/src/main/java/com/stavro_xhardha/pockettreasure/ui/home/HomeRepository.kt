package com.stavro_xhardha.pockettreasure.ui.home

import com.stavro_xhardha.pockettreasure.brain.*
import com.stavro_xhardha.pockettreasure.model.PrayerTimeResponse
import com.stavro_xhardha.pockettreasure.network.TreasureApi
import com.stavro_xhardha.rocket.Rocket
import retrofit2.Response
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val treasureApi: TreasureApi,
    private val mSharedPreferences: Rocket
) {
    suspend fun makePrayerCallAsync(): Response<PrayerTimeResponse> = treasureApi.getPrayerTimesTodayAsync(
        mSharedPreferences.readString(CAPITAL_SHARED_PREFERENCES_KEY),
        mSharedPreferences.readString(COUNTRY_SHARED_PREFERENCE_KEY)
    )

    suspend fun saveFajrTime(fajr: String) {
        if (fajr.isNotEmpty())
            mSharedPreferences.writeString(FAJR_KEY, fajr)
    }

    suspend fun saveDhuhrTime(dhuhr: String) {
        if (dhuhr.isNotEmpty())
            mSharedPreferences.writeString(DHUHR_KEY, dhuhr)
    }

    suspend fun saveAsrTime(asr: String) {
        if (asr.isNotEmpty())
            mSharedPreferences.writeString(ASR_KEY, asr)
    }

    suspend fun saveMagribTime(magrib: String) {
        if (magrib.isNotEmpty())
            mSharedPreferences.writeString(MAGHRIB_KEY, magrib)
    }

    suspend fun saveIshaTime(isha: String) {
        if (isha.isNotEmpty())
            mSharedPreferences.writeString(ISHA_KEY, isha)
    }

    suspend fun saveDayOfMonth(datyOfMonth: Int) {
        mSharedPreferences.writeInt(GREGORIAN_DAY_KEY, datyOfMonth)
    }

    suspend fun saveYear(year: Int) {
        if (year > 0)
            mSharedPreferences.writeInt(GREGORIAN_YEAR_KEY, year)
    }

    suspend fun saveMonthOfYear(month: Int) {
        if (month >= 0)
            mSharedPreferences.writeInt(GREGORIAN_MONTH_KEY, month)
    }

    suspend fun saveMonthName(monthNameInEnglish: String) {
        if (monthNameInEnglish.isNotEmpty())
            mSharedPreferences.writeString(GREGORIAN_MONTH_NAME_KEY, monthNameInEnglish)
    }

    suspend fun saveDayOfMonthHijri(day: String) {
        if (day.isNotEmpty())
            mSharedPreferences.writeString(HIRJI_DAY_OF_MONTH_KEY, day)
    }

    suspend fun saveMonthOfYearHijri(monthNameHijri: String) {
        if (monthNameHijri.isNotEmpty())
            mSharedPreferences.writeString(HIJRI_MONTH_NAME_KEY, monthNameHijri)
    }

    suspend fun saveYearHijri(year: String) {
        if (year.isNotEmpty())
            mSharedPreferences.writeString(HIJRI_YEAR_KEY, year)
    }

    suspend fun saveMidnight(midnight: String) {
        if (midnight.isNotEmpty())
            mSharedPreferences.writeString(MIDNIGHT_KEY, midnight)
    }

    suspend fun saveFinishFajrTime(sunrise: String) {
        mSharedPreferences.writeString(SUNRISE_KEY, sunrise)
    }

    suspend fun readMonthSection(): String? {
        val hijriDay = mSharedPreferences.readString(HIRJI_DAY_OF_MONTH_KEY)
        val hijriMonthName = mSharedPreferences.readString(HIJRI_MONTH_NAME_KEY)
        val hijriYear = mSharedPreferences.readString(HIJRI_YEAR_KEY)
        val gregorianDay = mSharedPreferences.readInt(GREGORIAN_DAY_KEY)
        val gregorianMonth = mSharedPreferences.readString(GREGORIAN_MONTH_NAME_KEY)
        val gregorianYear = mSharedPreferences.readInt(GREGORIAN_YEAR_KEY)

        return " $hijriDay $hijriMonthName $hijriYear / $gregorianDay $gregorianMonth $gregorianYear"
    }

    suspend fun readLocationSection(): String? {
        val capitalCity = mSharedPreferences.readString(CAPITAL_SHARED_PREFERENCES_KEY)
        val country = mSharedPreferences.readString(COUNTRY_SHARED_PREFERENCE_KEY)

        return " $capitalCity, $country"
    }

    suspend fun readFejrtime(): String? = mSharedPreferences.readString(FAJR_KEY)

    suspend fun readDhuhrTime(): String? = mSharedPreferences.readString(DHUHR_KEY)

    suspend fun readAsrTime(): String? = mSharedPreferences.readString(ASR_KEY)

    suspend fun readMaghribTime(): String? = mSharedPreferences.readString(MAGHRIB_KEY)

    suspend fun readIshaTime(): String? = mSharedPreferences.readString(ISHA_KEY)

    suspend fun readFinishFajrTime(): String? = mSharedPreferences.readString(SUNRISE_KEY)

    suspend fun getCurrentRegisteredDay(): Int = mSharedPreferences.readInt(GREGORIAN_DAY_KEY)

    suspend fun getCurrentRegisteredMonth(): Int = mSharedPreferences.readInt(GREGORIAN_MONTH_KEY)

    suspend fun getCurrentRegisteredYear(): Int = mSharedPreferences.readInt(GREGORIAN_YEAR_KEY)

    suspend fun countryHasBeenUpdated(): Boolean = mSharedPreferences.readBoolean(COUNTRY_UPDATED)

    suspend fun updateCountryState() {
        mSharedPreferences.writeBoolean(COUNTRY_UPDATED, false)
    }

    suspend fun isWorkerFired(): Boolean = mSharedPreferences.readBoolean(WORKER_FIRED_KEY)

    suspend fun updateWorkerFired() {
        mSharedPreferences.writeBoolean(WORKER_FIRED_KEY, true)
    }
}