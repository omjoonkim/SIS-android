package com.eighthour.makers.sis.libs.server

import com.eighthour.makers.sis.BuildConfig
import com.eighthour.makers.sis.libs.model.ChatItem
import com.eighthour.makers.sis.libs.model.Topic
import com.eighthour.makers.sis.libs.model.User
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface APIService {
    object EndPoint {
        const val baseUrl = BuildConfig.API_SERVER_IP
        const val wsUrl = BuildConfig.API_SERVER_SOCKET_IP
        fun wsUrl(topicId: Long?) = wsUrl + "ws/chat/" + topicId + "/"
    }

    @POST("/amoeba_chatting/api/users/")
    fun register(@Body body: User): Single<User>

    @POST("/amoeba_chatting/api/users/auth/")
    fun login(@Body body: User): Single<User>

    @DELETE("/amoeba_chatting/api/users/auth/")
    fun logout(): Completable

    @GET("/amoeba_chatting/api/topics?")
    fun getTopics(@Query("parent_id") parentTopicId: Long): Single<List<Topic>>

    @POST("/amoeba_chatting/api/topics/")
    fun postTopic(@Body topic: Topic): Single<Topic>

    @GET("/amoeba_chatting/api/chats/{topicId}?")
    fun getChats(@Path("topicId") parentTopicId: Long? = 1, @Query("from_id") fromId: Long? = null): Single<List<ChatItem>>

    @GET("/amoeba_chatting/api/favorites/")
    fun getFavorites(): Single<List<Topic>>

    @POST("/amoeba_chatting/api/favorites/")
    fun postFavoriteTopic(@Body body: PostFavoriteTopicBody): Completable

    @DELETE("/amoeba_chatting/api/favorites/{topicId}/")
    fun deleteFavorite(@Path("topicId") topicId: Long): Completable

    @POST("/amoeba_chatting/api/images/")
    fun postImage(@Body body: PostImageBody): Single<PostImageResponse>
}