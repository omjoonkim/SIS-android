package com.eighthour.makers.sis.libs.network

import com.eighthour.makers.sis.libs.network.model.ChatItem
import com.eighthour.makers.sis.libs.network.model.Topic
import com.eighthour.makers.sis.libs.network.model.User
import io.reactivex.Completable
import io.reactivex.Single

interface APIClientType {

    fun register(user: User): Single<User>

    fun logIn(user: User): Single<User>

    fun logOut(): Completable

    fun getTopics(parentTopicId: Long): Single<List<Topic>>

    fun postTopic(topic: Topic): Single<Topic>

    fun getFavorites() : Single<List<Topic>>

    fun postFavoriteTopic(topicId: Long): Completable

    fun deleteFavoriteTopic(topicId: Long): Completable

    fun getChats(topicId: Long = 1, fromChatId: Long? = null): Single<List<ChatItem>>

    fun postImage(image: String): Single<PostImageResponse>
}