package com.example.test12.retrofit


import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitServiceFactory {

    companion object{
        private const val waitTimeApiKey = "8e2cff00ff9c6b3f448294736de5908a"
        private const val flightApiKey = "2cfd0827f82ceaccae7882938b4b1627"
        private const val airportApiKey = "3035d833bb6e531654a3cce03e6b1fde"
        private val httpClient = OkHttpClient.Builder()
        private val retrofitBuilder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl("https://waittime.api.aero/waittime/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())

        private var retrofit = retrofitBuilder.build()

        fun  createWaitTimeService(): WaitTimeService{
            retrofitBuilder.baseUrl("https://waittime.api.aero/waittime/")
            httpClient.interceptors().clear()
            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("x-apiKey", waitTimeApiKey)
                    .build()
                chain.proceed(request)
            }

            retrofitBuilder.client(httpClient.build()) 
            retrofit = retrofitBuilder.build()

            return retrofit.create(WaitTimeService::class.java)
        }

        fun  createFlightInfoService(): FlightInfoService{
            retrofitBuilder.baseUrl("https://flifo-qa.api.aero/flifo/")
            httpClient.interceptors().clear()
            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("x-apiKey", flightApiKey)
                    .build()
                chain.proceed(request)
            }

            retrofitBuilder.client(httpClient.build())
            retrofit = retrofitBuilder.build()

            return retrofit.create(FlightInfoService::class.java)

        }

        fun createAirportService():AirportService{
            retrofitBuilder.baseUrl("https://data-qa.api.aero/data/")
            httpClient.interceptors().clear()
            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                        .header("x-apiKey", airportApiKey)
                        .build()
                chain.proceed(request)
            }

            retrofitBuilder.client(httpClient.build())
            retrofit = retrofitBuilder.build()

            return retrofit.create(AirportService::class.java)

        }
    }

    fun createGoogleDirectionsService(): GoogleDirectionsService{
        retrofitBuilder.baseUrl("//maps.googleapis.com/maps/api/")
        httpClient.interceptors().clear()
        retrofitBuilder.client(httpClient.build())
        retrofit = retrofitBuilder.build()

        return retrofit.create(GoogleDirectionsService::class.java)
    }


}