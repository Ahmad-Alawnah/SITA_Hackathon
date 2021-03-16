package com.example.test12

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.test12.databinding.ActivityMainBinding
import com.example.test12.retrofit.RetrofitServiceFactory
import com.example.test12.retrofit.Utils
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //test flight: RJ 168
        //test airportCode: MIA, AMM
        binding.btnTest.setOnClickListener {

            //test call to flight info API
            /*
            RetrofitServiceFactory.createFlightInfoService().getFlightByAirlineAndNumber("RJ", "168")
                .enqueue(object: Callback<JsonObject>{
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        val responseAsJSON = JSONObject(Gson().toJson(response.body()))
                        Toast.makeText(applicationContext, test.getString("success"), Toast.LENGTH_SHORT).show()
                        //binding.tvResult.text = responseAsJSON.toString(1)
                        val estimatedDepartureTime = Utils.getEstimatedDepartureFromRequest(responseAsJSON)
                        binding.tvResult.text = estimatedDepartureTime
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                    }


                })
             */

            //test call to wait time API
            /*RetrofitServiceFactory.createWaitTimeService().getWaitTimesForAirport("MIA") //AMM return 3 hours
                .enqueue(object: Callback<JsonObject>{
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        val responseAsJSON = JSONObject(Gson().toJson(response.body()))
                        val time = Utils.getSumOfLongestCheckpointsOrBufferTimeInMinutes(responseAsJSON)
                        binding.tvResult.text = time.toString()
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                    }

                })
             */

            //test call to airport API
            RetrofitServiceFactory.createAirportService().getAirportInfo("MIA")
                    .enqueue(object: Callback<JsonObject>{
                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                            val responseAsJSON = JSONObject(Gson().toJson(response.body()))
                            val latitudeAndLongitude = Utils.getLatitudeAndLongitude(responseAsJSON)
                            binding.tvResult.text = latitudeAndLongitude.toString()
                        }

                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                        }

                    })

            RetrofitServiceFactory.createGoogleDirectionsService().getDirections("Disneyland", "Universal", "AIzaSyCcXsi2vzNhRq2IalQfwaNoJ5dNSA84cx0")
                    .enqueue(object: Callback<JsonObject>{
                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                            val responseAsJSON = JSONObject(Gson().toJson(response.body()))
                            val latitudeAndLongitude = Utils.getLatitudeAndLongitude(responseAsJSON)
                            binding.tvResult.text = latitudeAndLongitude.toString()
                        }

                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                        }

                    })

        }

    }
}