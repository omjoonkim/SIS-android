package com.eighthour.makers.sis.libs.di

import com.eighthour.makers.sis.common.App
import com.eighthour.makers.sis.common.Environment
import com.eighthour.makers.sis.ui.*

interface AppComponentType {
    fun inject(activity: SplashActivity)
    fun inject(activity: MainActivity)
    fun inject(activity: LoginActivity)
    fun inject(activity: RegisterActivity)
    fun inject(activity: ChatRoomActivity)
    fun inject(activity: ChildTopicListActivity)
    fun inject(activity: TopicCreateActivity)
    fun inject(activity: LikeTopicsActivity)
    fun inject(app: App)
    fun enviorment(): Environment
}