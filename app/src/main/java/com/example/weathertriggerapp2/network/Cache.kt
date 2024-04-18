package com.example.weathertriggerapp2.network

import okhttp3.Interceptor

// https://blog.stackademic.com/cache-me-if-you-can-achieving-caching-with-retrofit-in-kotlin-e77b38a26417

/**
 * Method for updating cache on retrofit
 * */
val cachingInterceptor = Interceptor {
        chain ->
    val response = chain.proceed(chain.request())
    val cacheControl = response.header("Cache-Control")

    // if there are no cache headers, return original response
    if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
        cacheControl.contains("must-revalidate") || cacheControl.contains("max-age=0")
    ) { response }
    else {
        val maxAge = 60
        response.newBuilder()
            .header("Cache-Control", "public, max-age=$maxAge")
            .build()
    }
}
