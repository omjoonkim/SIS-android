package com.eighthour.makers.sis.libs.network.model

import com.google.gson.annotations.SerializedName

data class Topic(
        @SerializedName("parent_id")
        var parentId: Long = -1L,
        var id: Long = -1L,
        var name: String = "",
        @SerializedName("member_num")
        var memberNum: Int = 0,
        @SerializedName("subtopic_num")
        var subtopic_num: Int = 0,
        @SerializedName("is_favorite")
        var isFavorite: Boolean = false,
        @SerializedName("background_image_url")
        var backgroundImage: String? = null
)