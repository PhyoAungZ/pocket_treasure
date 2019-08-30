package com.stavro_xhardha.pockettreasure.ui.home

import android.view.View
import androidx.lifecycle.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.stavro_xhardha.pockettreasure.R
import com.stavro_xhardha.pockettreasure.brain.ACCENT_BACKGROUND
import com.stavro_xhardha.pockettreasure.brain.WHITE_BACKGROUND
import com.stavro_xhardha.pockettreasure.brain.decrementIdlingResource
import com.stavro_xhardha.pockettreasure.brain.incrementIdlingResource
import com.stavro_xhardha.pockettreasure.model.HomePrayerTime
import com.stavro_xhardha.pockettreasure.model.PrayerTimeResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.joda.time.LocalTime


class HomeViewModel @AssistedInject constructor(
    private val homeRepository: HomeRepository,
    @Assisted val savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedInject.Factory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): HomeViewModel
    }

    private val exceptionHandle = CoroutineExceptionHandler { _, _ ->
        decrementIdlingResource()
        showError()
    }

    private val workerExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    private val homePrayerData = ArrayList<HomePrayerTime>()
    private val _homeData = MutableLiveData<ArrayList<HomePrayerTime>>()
    private val _monthSection = MutableLiveData<String>()
    private val _locationSection = MutableLiveData<String>()
    private val _workManagerHasBeenFired = MutableLiveData<Boolean>()
    private val _progressBarVisibility = MutableLiveData<Int>()
    private val _showErrorToast = MutableLiveData<Boolean>()
    private val _contentVisibility = MutableLiveData<Int>()

    val progressBarVisibility: LiveData<Int> = _progressBarVisibility
    val showErrorToast: LiveData<Boolean> = _showErrorToast
    val contentVisibility: LiveData<Int> = _contentVisibility
    val workManagerHasBeenFired: LiveData<Boolean> = _workManagerHasBeenFired
    val monthSection: LiveData<String> = _monthSection
    val locationSection: LiveData<String> = _locationSection
    val homeData: LiveData<ArrayList<HomePrayerTime>> = _homeData

    fun loadPrayerTimes() {
        viewModelScope.launch(Dispatchers.Main + exceptionHandle) {
            if (dateHasPassed() || homeRepository.countryHasBeenUpdated()) {
                makePrayerApiCall()
            } else {
                withContext(Dispatchers.Main) {
                    setValuesToLiveData()
                }
            }
        }
    }

    private suspend fun makePrayerApiCall() {
        switchProgressBarOn()
        incrementIdlingResource()
        withContext(Dispatchers.IO) {
            val todaysPrayerTime = homeRepository.makePrayerCallAsync()
            if (todaysPrayerTime.isSuccessful) {
                decrementIdlingResource()
                saveDataToShardPreferences(todaysPrayerTime.body())
                withContext(Dispatchers.Main) {
                    setValuesToLiveData()
                }
            } else {
                withContext(Dispatchers.Main) {
                    decrementIdlingResource()
                    showError()
                }
            }
        }
    }

    private fun showError() {
        _showErrorToast.value = true
        _progressBarVisibility.value = View.GONE
        _contentVisibility.value = View.VISIBLE
    }

    private fun switchProgressBarOn() {
        _progressBarVisibility.value = View.VISIBLE
        _contentVisibility.value = View.GONE
        _showErrorToast.value = false
    }

    private fun switchProgressBarOff() {
        _progressBarVisibility.value = View.GONE
        _contentVisibility.value = View.VISIBLE
        _showErrorToast.value = false
    }

    private suspend fun setValuesToLiveData() {
        _monthSection.postValue(homeRepository.readMonthSection())
        _locationSection.postValue(homeRepository.readLocationSection())

        addDataToList()

        switchProgressBarOff()
    }

    private suspend fun addDataToList() {
        homePrayerData.add(
            HomePrayerTime(
                "Fajr",
                "${homeRepository.readFejrtime()} - ${homeRepository.readFinishFajrTime()}",
                WHITE_BACKGROUND,
                R.drawable.ic_fajr_sun
            )
        )

        homePrayerData.add(
            HomePrayerTime(
                "Dhuhr",
                homeRepository.readDhuhrTime() ?: "",
                WHITE_BACKGROUND,
                R.drawable.ic_dhuhr_sun
            )
        )

        homePrayerData.add(
            HomePrayerTime(
                "Asr",
                homeRepository.readAsrTime() ?: "",
                WHITE_BACKGROUND,
                R.drawable.ic_asr_sun
            )
        )

        homePrayerData.add(
            HomePrayerTime(
                "Maghrib",
                homeRepository.readMaghribTime() ?: "",
                WHITE_BACKGROUND,
                R.drawable.ic_magrib_sun
            )
        )


        homePrayerData.add(
            HomePrayerTime(
                "Isha",
                homeRepository.readIshaTime() ?: "",
                WHITE_BACKGROUND,
                R.drawable.ic_isha_sun
            )
        )

        findCurrentTime()

        _homeData.postValue(homePrayerData)
    }

    private fun findCurrentTime() {
        val currentTime = LocalTime()
        var currentColorNotFound = false
        homePrayerData.forEach {
            if (currentTime.isBefore(localTime(it.time)) && !currentColorNotFound) {
                it.backgroundColor = ACCENT_BACKGROUND
                currentColorNotFound = true
            } else {
                it.backgroundColor = WHITE_BACKGROUND
            }
        }
    }

    private fun localTime(timeOfPrayer: String): LocalTime = LocalTime(
        (timeOfPrayer.substring(0, 2).toInt()),
        (timeOfPrayer.substring(3, 5)).toInt()
    )

    private suspend fun dateHasPassed(): Boolean {
        val date = DateTime()
        return !(date.dayOfMonth == homeRepository.getCurrentRegisteredDay() &&
                date.monthOfYear == homeRepository.getCurrentRegisteredMonth() &&
                date.year == homeRepository.getCurrentRegisteredYear())
    }

    private suspend fun saveDataToShardPreferences(prayerTimeResponse: PrayerTimeResponse?) {
        if (prayerTimeResponse != null) {
            try {
                saveThePrayerTimeResponseToMemory(prayerTimeResponse)
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun saveThePrayerTimeResponseToMemory(prayerTimeResponse: PrayerTimeResponse) {
        prayerTimeResponse.let {
            homeRepository.saveFajrTime(it.data.timings.fajr)
            homeRepository.saveFinishFajrTime(it.data.timings.sunrise)
            homeRepository.saveDhuhrTime(it.data.timings.dhuhr)
            homeRepository.saveAsrTime(it.data.timings.asr)
            homeRepository.saveMagribTime(it.data.timings.magrib)
            homeRepository.saveIshaTime(it.data.timings.isha)
            homeRepository.saveDayOfMonth(it.data.date.gregorianDate.day.toInt())
            homeRepository.saveYear(it.data.date.gregorianDate.year.toInt())
            homeRepository.saveMonthOfYear(it.data.date.gregorianDate.gregorianMonth.number)
            homeRepository.saveMonthName(it.data.date.gregorianDate.gregorianMonth.monthNameInEnglish)
            homeRepository.saveDayOfMonthHijri(it.data.date.hijriPrayerDate.day)
            homeRepository.saveMonthOfYearHijri(it.data.date.hijriPrayerDate.hirjiMonth.monthNameInEnglish)
            homeRepository.saveYearHijri(it.data.date.hijriPrayerDate.year)
            homeRepository.saveMidnight(it.data.timings.midnight)
            homeRepository.updateCountryState()
        }
    }

    fun initWorker() {
        viewModelScope.launch(Dispatchers.IO + workerExceptionHandler) {
            val isWorkerFired = homeRepository.isWorkerFired()
            _workManagerHasBeenFired.postValue(isWorkerFired)
        }
    }

    fun updateWorkManagerFiredState() {
        viewModelScope.launch {
            homeRepository.updateWorkerFired()
        }
    }
}