package com.example.oliverboardtest.api.exception

open class NoConnectivityException(message: String?) : RuntimeException(message) {

    fun getLocalNetworkErrorBody(): ErrorBody {
        return ErrorBody("500", "Please check your network connection and try again.")
    }
}

data class ErrorBody(val code: String, val message: String)