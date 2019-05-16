package com.stavro_xhardha.pockettreasure.dependency_injection

import android.app.Application
import com.stavro_xhardha.pockettreasure.network.TreasureApi
import com.stavro_xhardha.rocket.Rocket
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [NetworkModule::class, PreferencesModule::class])
interface PocketTreasureComponent {
    fun getTreasureApi(): TreasureApi

    fun getSharedPreferences(): Rocket

    @Component.Factory
    interface Builder {
        fun create(@BindsInstance pocketTreasureApplication: Application): PocketTreasureComponent
    }
}