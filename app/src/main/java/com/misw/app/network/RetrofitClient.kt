package com.misw.app.network

import com.misw.app.BuildConfig
import com.misw.app.network.musician.MusicianApiService
import com.misw.app.network.prize.PrizeApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = BuildConfig.BASE_URL

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                EspressoIdlingResource.increment()
                try {
                    chain.proceed(chain.request())
                } finally {
                    EspressoIdlingResource.decrement()
                }
            }
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val albumApiService: AlbumApiService by lazy {
        retrofit.create(AlbumApiService::class.java)
    }

    val musicianApiService: MusicianApiService by lazy {
        retrofit.create(MusicianApiService::class.java)
    }

    val prizeApiService: PrizeApiService by lazy {
        retrofit.create(PrizeApiService::class.java)
    }
}