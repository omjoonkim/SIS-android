package com.eighthour.makers.sis.common

import com.bumptech.glide.RequestManager
import com.eighthour.makers.sis.libs.data.ChatRepositoryType
import com.eighthour.makers.sis.libs.data.TopicRepositoryType
import com.eighthour.makers.sis.libs.network.APIClientType
import com.google.gson.Gson

data class Environment(
        var apiClient: APIClientType,
        var currentUser: CurrentUser,
        var topicRepository: TopicRepositoryType,
        var chatRepository: ChatRepositoryType,
        var requestManager: RequestManager,
        var gson: Gson
)
