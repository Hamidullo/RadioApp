package com.alexandr_yurchenko.radiobeach.client

import com.alexandr_yurchenko.radiobeach.model.deep.RadioDeep
import com.alex_yurchenko.radiobeach.model.history.RadioHistory
import com.alexandr_yurchenko.radiobeach.model.main.RadioMain
import com.alexandr_yurchenko.radiobeach.model.online.RadioOnline
import com.alexandr_yurchenko.radiobeach.model.relax.RadioRelax
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitServices {
    @GET("1632/status.json")
    fun getRadioMain(): Call<RadioMain>

    @GET("8382/status.json")
    fun getRadioOnline(): Call<RadioOnline>

    @GET("31825/status.json")
    fun getRadioDeep(): Call<RadioDeep>

    @GET("station30/status.json")
    fun getRadioHistory(): Call<RadioHistory>

    @GET("9427/status.json")
    fun getRadioRelax(): Call<RadioRelax>
}