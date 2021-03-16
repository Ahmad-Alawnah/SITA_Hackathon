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
            /*RetrofitServiceFactory.createAirportService().getAirportInfo("MIA")
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

            */

            val points = ArrayList<String>()
            //sample data
            points.add("31.989143549718108,35.86185666225099") //origin (my house)
            //points.add("31.990878495552742,35.86631239846654") //destination (Amman mall)
            points.add("32.024086035555264,35.87628902029236") //destination (PSUT)
            //if you want to test other locations, go to google maps, right click somewhere and copy the latitude and longitude

            RetrofitServiceFactory.createGraphHopperService().getDirections(points, "6f127006-930a-4e51-8c5b-891c5adb7fc2")
                    .enqueue(object : Callback<JsonObject> {
                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                            val responseAsJSON = JSONObject(Gson().toJson(response.body()))
                            binding.tvResult.text = Utils.getTimeInMinutesFromGraphHopperResponse(responseAsJSON).toString()
                        }

                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                            binding.tvResult.text = t.toString()
                        }

                    })
        }

    }
}