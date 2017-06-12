package com.eighthour.makers.sis.libs.model

import com.google.gson.annotations.SerializedName

data class User(
        var id : Long? = null,
        var username: String? = null,
        var email: String? = null,
        var password: String? = null,
        var token: String? = null,
        @SerializedName("profile_image_url")
        var profileUrl: String? = null
)

