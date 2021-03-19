package com.example.sitahackathon.retrofit

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface AirportService {

    @GET("v3/airports/{airportCode}")
    fun getAirportInfo(@Path("airportCode") airportCode: String): Call<JsonObject>
}