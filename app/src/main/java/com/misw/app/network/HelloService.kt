package com.misw.app.network

import retrofit2.Call
import retrofit2.http.GET

data class HelloResponse(val message: String)

interface HelloService {
    @GET("greeting")
    fun getGreeting(): Call<HelloResponse>
}
