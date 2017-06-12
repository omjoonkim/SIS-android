package com.eighthour.makers.sis.libs.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceManager(val context: Context?) {

    val prefs: SharedPreferences? by lazy { context?.getSharedPreferences("common", Context.MODE_PRIVATE) }

    var userId: String?
        get() = prefs?.getString("userId", null)
        set(value) {
            prefs?.edit()?.let {
                if (value == null)
                    it.remove("userId")
                else
                    it.putString("userId", value)
                it.commit()
            }
        }

    var token: String?
        get() = prefs?.getString("token", null)
        set(value) {
            prefs?.edit()?.let {
                if (value == null)
                    it.remove("token")
                else
                    it.putString("token", value)
                it.commit()
            }
        }

    var email: String?
        get() = prefs?.getString("email", null)
        set(value) {
            prefs?.edit()?.let {
                if (value == null)
                    it.remove("email")
                else
                    it.putString("email", value)
                it.commit()
            }
        }

    var name: String?
        get() = prefs?.getString("name", null)
        set(value) {
            prefs?.edit()?.let {
                if (value == null)
                    it.remove("name")
                else
                    it.putString("name", value)
                it.commit()
            }
        }
}