package com.eighthour.makers.sis.libs.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.eighthour.makers.sis.common.App
import com.eighthour.makers.sis.common.CurrentUser
import com.eighthour.makers.sis.common.Environment
import com.eighthour.makers.sis.libs.data.ChatRepository
import com.eighthour.makers.sis.libs.data.ChatRepositoryType
import com.eighthour.makers.sis.libs.data.TopicRepository
import com.eighthour.makers.sis.libs.data.TopicRepositoryType
import com.eighthour.makers.sis.libs.server.APIClient
import com.eighthour.makers.sis.libs.server.APIClientType
import com.eighthour.makers.sis.libs.server.APIRequestInterceptor
import com.eighthour.makers.sis.libs.server.APIService
import com.eighthour.makers.sis.libs.util.SharedPreferenceManager
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule(val app: App) {

    @Provides
    @Singleton
    fun provideApplicationContext(): Context = app.applicationContext

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()


    @Provides
    @Singleton
    fun provideApiClient(apiService: APIService): APIClientType = APIClient(apiService)

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor, apiRequestInterceptor: APIRequestInterceptor): OkHttpClient =
            OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .addInterceptor(apiRequestInterceptor)
                    .build()

    @Provides
    @Singleton
    fun provideApiReuqestInterceptor(currentUser: CurrentUser) = APIRequestInterceptor(currentUser)

    @Provides
    @Singleton
    fun provideSharedPreferenceManager(context: Context) = SharedPreferenceManager(context)

    @Provides
    @Singleton
    fun provideCurrentUser(sharedPreferenceManager: SharedPreferenceManager) = CurrentUser(sharedPreferenceManager = sharedPreferenceManager)

    @Provides
    @Singleton
    fun provideApiService(gson: Gson, okhttpCLient: OkHttpClient): APIService =
            Retrofit.Builder()
                    .client(okhttpCLient)
                    .baseUrl(APIService.EndPoint.baseUrl)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(APIService::class.java)

    @Provides
    @Singleton
    fun topicRepository(apiClient: APIClientType): TopicRepositoryType = TopicRepository(apiClient)

    @Provides
    @Singleton
    fun chatRepository(apiClient: APIClientType): ChatRepositoryType = ChatRepository(apiClient)

    @Provides
    @Singleton
    fun requestManager(context: Context): RequestManager = Glide.with(context)

    @Provides
    @Singleton
    fun provideEnviorment(apiClient: APIClientType, currentUser: CurrentUser,
                          topicRepository: TopicRepositoryType, chatRepository: ChatRepositoryType,
                          requestManager: RequestManager, gson: Gson): Environment
            = Environment(apiClient, currentUser, topicRepository, chatRepository, requestManager, gson)

}