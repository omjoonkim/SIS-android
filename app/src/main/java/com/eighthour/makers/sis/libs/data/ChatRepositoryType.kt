package com.eighthour.makers.sis.libs.data

import com.eighthour.makers.sis.libs.network.model.ChatItem
import io.reactivex.Single


interface ChatRepositoryType {
    fun loadChats(topicId: Long): Single<List<ChatItem>>
    fun loadBeforeChat(topicId: Long): Single<List<ChatItem>>
    fun addChat(topicId: Long, chat: ChatItem)
}