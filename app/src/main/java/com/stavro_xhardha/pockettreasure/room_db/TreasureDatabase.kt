package com.stavro_xhardha.pockettreasure.room_db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stavro_xhardha.pockettreasure.model.*

@Database(
    entities = [Name::class, Surah::class, Aya::class, Country::class, PrayerTiming::class],
    version = 1,
    exportSchema = false
)
abstract class TreasureDatabase : RoomDatabase() {
    abstract fun namesDao(): NamesDao

    abstract fun surahsDao(): SurahsDao

    abstract fun ayasDao(): AyasDao

    abstract fun countriesDao(): CountriesDao

    abstract fun prayerTimesDao(): PrayerTimesDao
}