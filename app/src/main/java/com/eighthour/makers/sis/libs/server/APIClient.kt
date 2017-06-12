package com.eighthour.makers.sis.libs.server

import com.eighthour.makers.sis.libs.model.ChatItem
import com.eighthour.makers.sis.libs.model.Topic
import com.eighthour.makers.sis.libs.model.User
import com.eighthour.makers.sis.libs.util.toBase64
import com.eighthour.makers.sis.libs.util.toFile
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class APIClient(val apiService: APIService) : APIClientType {

    override fun register(user: User): Single<User> =
            apiService.register(user)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun logIn(user: User): Single<User> =
            apiService.login(user)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun logOut(): Completable =
            apiService.logout()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun getTopics(parentTopicId: Long): Single<List<Topic>> =
            apiService.getTopics(parentTopicId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun postTopic(topic: Topic): Single<Topic> =
            (if (topic.backgroundImage != null)
                postImage(topic.backgroundImage!!)
                        .flatMap { apiService.postTopic(topic.copy(backgroundImage = it.imageUrl)) }
            else
                apiService.postTopic(topic))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun getFavorites(): Single<List<Topic>> =
            apiService.getFavorites()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun postFavoriteTopic(topicId: Long): Completable =
            apiService.postFavoriteTopic(PostFavoriteTopicBody(topicId))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun deleteFavoriteTopic(topicId: Long): Completable =
            apiService.deleteFavorite(topicId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())


    override fun getChats(topicId: Long, fromChatId: Long?): Single<List<ChatItem>> =
            apiService.getChats(topicId, if (fromChatId == -1L) null else fromChatId)
                    .map { it.reversed() }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun postImage(image: String): Single<PostImageResponse> =
            apiService.postImage(PostImageBody(image.toFile()?.toBase64()))

}