package com.eighthour.makers.sis.libs.data

import com.eighthour.makers.sis.libs.model.Topic
import com.eighthour.makers.sis.libs.rx.neverError
import com.eighthour.makers.sis.libs.server.APIClientType
import io.reactivex.Single
import io.reactivex.rxkotlin.toSingle

class TopicRepository(val apiClient: APIClientType) : TopicRepositoryType {

    val topicMap = hashMapOf<Long, Topic>()

    val topicList get() = topicMap.values.toMutableList()

    var isLoadedLikesTopic = false


    override fun loadTopics(parentTopicId: Long, force: Boolean): Single<List<Topic>> =
            if (force || topicMap.values.find { it.parentId == parentTopicId } == null)
                apiClient.getTopics(parentTopicId)
                        .doOnSuccess { it.forEach { topicMap.put(it.id, it) } }
                        .map { topicList.filter { it.parentId == parentTopicId } }
            else
                topicList.filter { it.parentId == parentTopicId }.toSingle()

    override fun getTopic(topicId: Long) = topicMap[topicId]

    override fun addTopic(topic: Topic): Single<Topic> =
            apiClient.postTopic(topic)
                    .doOnSuccess {
                        topicMap[it.parentId]?.memberNum?.let { num ->
                            topicMap[it.parentId]?.memberNum = num + 1
                        }
                        topicMap.put(it.id, it)
                    }

    override fun changeFavorite(topicId: Long): Boolean {
        val isfavorite = topicMap[topicId]?.isFavorite ?: false
        if (isfavorite)
            removeFavoriteTopic(topicId)
        else
            addFavoriteTopic(topicId)
        return isfavorite.not()
    }

    override fun loadTopicOfLike(force: Boolean): Single<List<Topic>> =
            if (force || isLoadedLikesTopic.not())
                apiClient.getFavorites()
                        .doOnSuccess {
                            topicMap.putAll(it.map { it.id to it })
                            isLoadedLikesTopic = true
                        }
            else
                topicList.filter { it.isFavorite }.toSingle()

    private fun addFavoriteTopic(topicId: Long) {
        apiClient.postFavoriteTopic(topicId)
                .neverError()
                .subscribe { topicMap[topicId]?.isFavorite = true }
    }

    private fun removeFavoriteTopic(topicId: Long) {
        apiClient.deleteFavoriteTopic(topicId)
                .neverError()
                .subscribe { topicMap[topicId]?.isFavorite = false }
    }
}