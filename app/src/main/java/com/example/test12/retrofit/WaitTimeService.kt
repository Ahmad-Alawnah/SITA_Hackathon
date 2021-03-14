package com.example.test12.retrofit

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WaitTimeService {

    @GET("v1/current/{airportCode}")
    fun getWaitTimesForAirport(@Path("airportCode") airportCode: String): Call<JsonObject>
}