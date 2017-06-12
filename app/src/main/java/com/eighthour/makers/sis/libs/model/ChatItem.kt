package com.eighthour.makers.sis.libs.model

import com.eighthour.makers.sis.libs.util.toDate
import com.eighthour.makers.sis.libs.util.toTimeText
import com.google.gson.annotations.SerializedName

data class ChatItem(
        var id: Long = -1,
        var user: User? = null,
        var content: String = "",
        @SerializedName("topic_id")
        var topicId: Long = -1,
        @SerializedName("timestamp")
        var timeStamp: String? = null
) {
    val dateTime: String? get() = timeStamp?.toDate()?.toTimeText()
}