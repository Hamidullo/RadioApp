package com.alexandr_yurchenko.radiobeach.client

object Common {
    private val BASE_URL = "http://myradio24.com/users/"
    val retrofitService: RetrofitServices
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitServices::class.java)
}