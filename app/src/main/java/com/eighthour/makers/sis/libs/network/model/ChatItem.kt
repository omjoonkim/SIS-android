package com.eighthour.makers.sis.libs.network.model

import com.eighthour.makers.sis.libs.util.toDate
import com.eighthour.makers.sis.libs.util.toTimeText

data class ChatItem(
        var id: Long = -1,
        var user: User? = null,
        var content: String = "",
        @com.google.gson.annotations.SerializedName("topic_id")
        var topicId: Long = -1,
        @com.google.gson.annotations.SerializedName("timestamp")
        var timeStamp: String? = null
) {
    val dateTime: String? get() = timeStamp?.toDate()?.toTimeText()
}