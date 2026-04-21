package com.misw.app.repository

import com.misw.app.network.HelloServiceAdapter

class HelloRepository(
    private val serviceAdapter: HelloServiceAdapter = HelloServiceAdapter()
) {
    fun getGreeting(): String {
        return serviceAdapter.fetchGreeting()
    }
}
