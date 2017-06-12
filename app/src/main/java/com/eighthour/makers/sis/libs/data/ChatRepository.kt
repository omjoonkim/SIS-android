package com.eighthour.makers.sis.libs.data

import com.eighthour.makers.sis.libs.model.ChatItem
import com.eighthour.makers.sis.libs.server.APIClientType
import io.reactivex.Single

class ChatRepository(val apiClient: APIClientType) : ChatRepositoryType {

    private val chatMap = hashMapOf<Long, MutableList<ChatItem>>()

    private val isDisableMoreLoadSet = hashSetOf<Long>()

    override fun loadChats(topicId: Long): Single<List<ChatItem>> =
            apiClient.getChats(topicId)
                    .doOnSuccess {
                        if (it.size < 20)
                            isDisableMoreLoadSet.add(topicId)
                        chatMap.put(topicId, it.toMutableList())
                    }

    override fun loadBeforeChat(topicId: Long): Single<List<ChatItem>> =
            Single.just(isDisableMoreLoadSet.contains(topicId).not()
                    && chatMap.containsKey(topicId) && chatMap[topicId]!!.isNotEmpty())
                    .filter { it }
                    .flatMapSingle { apiClient.getChats(topicId, chatMap[topicId]?.first()!!.id) }
                    .doOnSuccess {
                        if (it.size < 20)
                            isDisableMoreLoadSet.add(topicId)
                        chatMap[topicId]!!.addAll(0, it)
                    }

    override fun addChat(topicId: Long, chat: ChatItem) {
        chatMap[topicId]?.add(chat)
    }
}