package com.misw.app.network

import com.misw.app.BuildConfig
import com.misw.app.network.album.AlbumApiService
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
        _albumApiService = null
        _musicianApiService = null
        _collectorApiService = null
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

    private var _albumApiService: AlbumApiService? = null
    val albumApiService: AlbumApiService
        get() = _albumApiService ?: retrofit.create(AlbumApiService::class.java).also { _albumApiService = it }

    private var _musicianApiService: MusicianApiService? = null
    val musicianApiService: MusicianApiService
        get() = _musicianApiService ?: retrofit.create(MusicianApiService::class.java).also { _musicianApiService = it }

    private var _collectorApiService: CollectorApiService? = null
    val collectorApiService: CollectorApiService
        get() = _collectorApiService ?: retrofit.create(CollectorApiService::class.java).also { _collectorApiService = it }
}