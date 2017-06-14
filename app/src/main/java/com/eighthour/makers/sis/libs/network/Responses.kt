package com.eighthour.makers.sis.libs.network

import com.google.gson.annotations.SerializedName


data class PostImageResponse(@SerializedName("image_url") val imageUrl: String? = null)