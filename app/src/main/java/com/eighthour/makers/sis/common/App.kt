package com.eighthour.makers.sis.common

import android.app.Application
import com.eighthour.makers.sis.libs.di.AppComponent
import com.eighthour.makers.sis.libs.di.AppModule
import com.eighthour.makers.sis.libs.di.DaggerAppComponent

class App : Application() {

    companion object {
        lateinit var instance: App
    }

    val component: AppComponent by lazy { DaggerAppComponent.builder().appModule(AppModule(this)).build() }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        component.inject(this)
    }
}