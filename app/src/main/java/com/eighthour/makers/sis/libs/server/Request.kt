package com.eighthour.makers.sis.libs.server

import com.google.gson.annotations.SerializedName

/**
 * Created by Omjoon on 2017. 6. 7..
 */

data class PostImageBody(val file : String?)

data class PostFavoriteTopicBody(@SerializedName("topic_id") val topicId : Long)