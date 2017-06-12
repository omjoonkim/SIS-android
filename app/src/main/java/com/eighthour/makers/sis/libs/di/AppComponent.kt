package com.eighthour.makers.sis.libs.di

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent : AppComponentType