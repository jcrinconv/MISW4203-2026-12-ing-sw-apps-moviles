package com.misw.app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HelloServiceAdapter {

    private val BASE_URL = "https://api.example.com/"

    private val service: HelloService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HelloService::class.java)
    }

    fun fetchGreeting(): String {
        return try {
            val response = service.getGreeting().execute()
            if (response.isSuccessful) {
                response.body()?.message ?: "Hola Mundo"
            } else {
                "Hola Mundo"
            }
        } catch (e: Exception) {
            "Hola Mundo"
        }
    }
}
