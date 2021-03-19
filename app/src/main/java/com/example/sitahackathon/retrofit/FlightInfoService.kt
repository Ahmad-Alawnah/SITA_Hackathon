package com.example.sitahackathon.retrofit

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface FlightInfoService {

    @GET("flightinfo/v1/flights/airline/{airlineCode}/flightNumber/{flightNumber}")
    fun getFlightByAirlineAndNumber(@Path("airlineCode") airlineCode: String,
                                    @Path("flightNumber") flightNumber: String): Call<JsonObject>
    //Might change flightNumber to int based on what's provided by the UI

}