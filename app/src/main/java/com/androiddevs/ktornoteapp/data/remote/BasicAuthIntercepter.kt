package com.androiddevs.ktornoteapp.data.remote

import com.androiddevs.ktornoteapp.other.Constants.ignoreAuthUrls
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthIntercepter :Interceptor {

    var email: String? = null
    var password: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if(request.url.encodedPath in ignoreAuthUrls){
            return chain.proceed(request)
        }
        val authenticatedRequest = request.newBuilder()
            .header("Authorization",Credentials.basic(email?:"",password?:""))
            .build()
        return chain.proceed(authenticatedRequest)
    }
}