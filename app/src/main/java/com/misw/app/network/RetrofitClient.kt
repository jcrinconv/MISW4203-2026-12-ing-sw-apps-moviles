package com.misw.app.network

import com.misw.app.BuildConfig
import com.misw.app.network.musician.MusicianApiService
import com.misw.app.network.collector.CollectorApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var baseUrl = BuildConfig.BASE_URL

    fun setBaseUrl(url: String) {
        baseUrl = url
        _retrofit = null
    }

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

    private var _retrofit: Retrofit? = null
    private val retrofit: Retrofit
        get() = _retrofit ?: synchronized(this) {
            _retrofit ?: Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build().also { _retrofit = it }
        }

    val albumApiService: AlbumApiService get() = retrofit.create(AlbumApiService::class.java)

    val musicianApiService: MusicianApiService get() = retrofit.create(MusicianApiService::class.java)

    val collectorApiService: CollectorApiService get() = retrofit.create(CollectorApiService::class.java)
}