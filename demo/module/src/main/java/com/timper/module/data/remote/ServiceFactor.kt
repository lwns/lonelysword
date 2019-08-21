package com.timper.module.data.remote

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * User: tangpeng.yang
 * Date: 20/03/2018
 * Description:
 * FIXME
 */
class ServiceFactor private constructor() {
    companion object {
        val instance = SingletonHolder.holder
    }

    private object SingletonHolder {
        val holder = ServiceFactor()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.wanandroid.com")
            .client(createOkHttpClient(true, createLoggingInterceptor(true)))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .build()
    }

    fun <T> createService(clazz: Class<T>): T {
        return retrofit.create(clazz)
    }

    private fun createOkHttpClient(isDebug: Boolean?, httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(httpLoggingInterceptor)
            .addInterceptor(createHeadInterceptor())
        isDebug?.let {
            if (it) {
                builder.addNetworkInterceptor(StethoInterceptor())
            }
        }
        builder.retryOnConnectionFailure(true)
        return builder.connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    private fun createGson(): Gson {
        return GsonBuilder().setLenient()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .create()
    }

    private fun createHeadInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Content-Type", "application/json;charset=UTF-8")
                .method(original.method(), original.body())
                .build()
            chain.proceed(request)
        }
    }

    private fun createLoggingInterceptor(isDebug: Boolean): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        if (isDebug) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logging.level = HttpLoggingInterceptor.Level.NONE
        }
        return logging
    }
}
