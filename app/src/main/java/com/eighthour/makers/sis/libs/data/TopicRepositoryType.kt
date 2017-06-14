package com.eighthour.makers.sis.libs.data

import com.eighthour.makers.sis.libs.network.model.Topic
import io.reactivex.Single


interface TopicRepositoryType {

    fun loadTopics(parentTopicId: Long = 1,force : Boolean = false): Single<List<Topic>>
    fun getTopic(topicId: Long): Topic?
    fun addTopic(topic: Topic): Single<Topic>
    fun changeFavorite(topicId: Long): Boolean
    fun loadTopicOfLike(force: Boolean = false): Single<List<Topic>>
}