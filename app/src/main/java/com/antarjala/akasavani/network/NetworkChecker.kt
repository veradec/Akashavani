package com.antarjala.akasavani.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

class NetworkChecker {
    suspend fun isUrlAccessible(url: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = TimeUnit.SECONDS.toMillis(5).toInt()
            connection.readTimeout = TimeUnit.SECONDS.toMillis(5).toInt()
            connection.requestMethod = "HEAD"
            connection.connect()
            val responseCode = connection.responseCode
            connection.disconnect()
            responseCode in 200..399
        } catch (e: Exception) {
            false
        }
    }
} 