package com.example.test12.retrofit

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GraphHopperService {

    @GET("route?vehicle=car")
    fun getDirections(@Query("point", encoded = true) points: ArrayList<String>,
                      @Query("key") apiKey: String): Call<JsonObject>
}