package com.eighthour.makers.sis.common

import com.eighthour.makers.sis.libs.model.User
import com.eighthour.makers.sis.libs.util.SharedPreferenceManager

data class CurrentUser(var user: User? = null, val sharedPreferenceManager: SharedPreferenceManager) {

    val id: Long?
        get() = user?.id

    init {
        if (user == null)
            if (sharedPreferenceManager.email != null && sharedPreferenceManager.name != null
                    && sharedPreferenceManager.token != null)
                user = User(id = sharedPreferenceManager.userId?.toLong(), email = sharedPreferenceManager.email, username = sharedPreferenceManager.name, token = sharedPreferenceManager.token)
    }

    fun login(user: User) {
        this.user = user
        sharedPreferenceManager.userId = user.id?.toString()
        sharedPreferenceManager.email = user.email
        sharedPreferenceManager.name = user.username
        sharedPreferenceManager.token = user.token
    }

    fun logout() {
        user = null
        sharedPreferenceManager.userId = null
        sharedPreferenceManager.email = null
        sharedPreferenceManager.name = null
        sharedPreferenceManager.token = null
    }
}