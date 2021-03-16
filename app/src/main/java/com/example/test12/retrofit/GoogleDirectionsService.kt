package com.example.test12.retrofit

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//TODO: NEEDS TESTING
interface GoogleDirectionsService {

    @GET("directions/json")
    fun getDirections(@Query("origin") origin:String,
                      @Query("destination") destination: String,
                      @Query("key") key: String): Call<JsonObject>

}