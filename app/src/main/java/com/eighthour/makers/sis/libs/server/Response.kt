package com.eighthour.makers.sis.libs.server

import com.google.gson.annotations.SerializedName


data class PostImageResponse(@SerializedName("image_url") val imageUrl: String? = null)