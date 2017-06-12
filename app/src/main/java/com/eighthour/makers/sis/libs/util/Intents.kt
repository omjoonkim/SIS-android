package com.eighthour.makers.sis.libs.util

import android.app.Activity
import android.content.Intent

class Intents {
    companion object {
        const val TOPIC_ID = "topicId"

        const val REQUEST_REGISTER = 1

        const val REQUEST_SELECT_PHOTO = 2
    }
}

fun Activity.startActivityWithFinish(`class`: Class<*>) = startActivity(Intent(this, `class`)).run { finish() }

fun Activity.startActivityWithFinish(intent: Intent) = startActivity(intent).run { finish() }

fun Activity.startActivity(`class`: Class<*>) = startActivity(Intent(this, `class`))

fun Activity.startActivityForResult(`class`: Class<*>, requestCode: Int) = startActivityForResult(Intent(this, `class`), requestCode)

fun Intent.getLongExtra(key: String) = getLongExtra(key, -1L)

fun Intent.getIntExtra(key: String) = getIntExtra(key, -1)
