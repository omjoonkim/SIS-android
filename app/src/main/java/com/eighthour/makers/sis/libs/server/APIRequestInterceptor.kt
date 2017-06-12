package com.eighthour.makers.sis.libs.server

import com.eighthour.makers.sis.common.CurrentUser
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

open class APIRequestInterceptor(val currentUser: CurrentUser) : Interceptor {


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response =
            if (currentUser.user?.token != null)
                chain.proceed(reqest(chain.request()))
            else
                chain.proceed(chain.request())

    private fun reqest(request: Request): Request =
            request.newBuilder()
                    .addHeader("Authorization","token ${currentUser.user?.token}")
                    .addHeader("Content-Type", "application/json")
                    .url(request.url())
                    .build()

}