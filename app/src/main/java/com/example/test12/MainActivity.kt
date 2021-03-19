package com.example.test12

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.test12.databinding.ActivityMainBinding
import com.example.test12.retrofit.RetrofitServiceFactory
import com.example.test12.retrofit.Utils
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.internal.Util
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainResultLinearLayout.visibility = View.INVISIBLE

        //test flight: RJ 168
        //test airportCode: MIA, AMM
        binding.calculateButton.setOnClickListener {

            binding.errorMessageTextView.visibility = View.GONE
            val airlineCode = binding.airlineCodeEditText.text.toString()
            val flightNumber = binding.flightNumberEditText.text.toString()

            if (airlineCode.isEmpty()) {
                binding.airlineCodeEditText.setError("This Field is Required")
                return@setOnClickListener
            }

            if (flightNumber.isEmpty()) {
                binding.flightNumberEditText.setError("This Field is Required")
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            RetrofitServiceFactory.createFlightInfoService().getFlightByAirlineAndNumber(
                airlineCode,
                flightNumber
            )
                .enqueue(object : Callback<JsonObject> {
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {

                        val responseAsJSON = JSONObject(Gson().toJson(response.body()))
                        if (responseAsJSON["success"].equals(false)) {
                            binding.progressBar.visibility = View.GONE
                            binding.errorMessageTextView.visibility = View.VISIBLE
                            return
                        }

                        val departureAirportCode = Utils.getDepartureAirportCode(responseAsJSON)
                        val estimatedDepartureTime = Utils.getEstimatedDepartureFromRequest(
                            responseAsJSON
                        )
                        val zonedDateTime = Utils.convertStringToZonedDateTime(
                            estimatedDepartureTime
                        )
                        val offsetDateTime = zonedDateTime.toOffsetDateTime().withOffsetSameInstant(
                            OffsetDateTime.now().offset
                        )

                        RetrofitServiceFactory.createAirportService().getAirportInfo(departureAirportCode).enqueue(object: Callback<JsonObject> {

                            override fun onResponse(
                                call: Call<JsonObject>,
                                response: Response<JsonObject>
                            ) {
                                val responseAsJSON = JSONObject(Gson().toJson(response.body()))
                                val latitudeAndLongitude = Utils.getLatitudeAndLongitude(responseAsJSON)

                                if (responseAsJSON["success"].equals(false)) {
                                    binding.progressBar.visibility = View.GONE
                                    binding.errorMessageTextView.visibility = View.VISIBLE
                                    return
                                }

                                RetrofitServiceFactory.createWaitTimeService().getWaitTimesForAirport("MIA") //AMM return 3 hours
                                    .enqueue(object: Callback<JsonObject>{
                                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                                            val responseAsJSON = JSONObject(Gson().toJson(response.body()))
                                            val time = Utils.getSumOfLongestCheckpointsOrBufferTimeInMinutes(responseAsJSON)

                                            // Current location
                                            val points = ArrayList<String>()
                                            points.add("31.989143549718108,35.86185666225099") // TODO get user current location
                                            points.add(latitudeAndLongitude)
                                            RetrofitServiceFactory.createGraphHopperService().getDirections(points, "6f127006-930a-4e51-8c5b-891c5adb7fc2")
                                                .enqueue(object : Callback<JsonObject> {
                                                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                                                        Log.e("JSON", response.body().toString())

                                                        if (!response.isSuccessful) {
                                                            binding.progressBar.visibility = View.GONE
                                                            binding.errorMessageTextView.visibility = View.VISIBLE
                                                            return
                                                        }

                                                        val responseAsJSON = JSONObject(Gson().toJson(response.body()))

                                                        binding.tvResult.text =
                                                            DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a").format(
                                                                offsetDateTime
                                                            )
                                                        offsetDateTime.minusMinutes(5 /*PUT ALL MINUTES HERE AND DISPLAY THE RESULT*/)
                                                        //WORKING WITH DATE/TIME OBJECTS IS FUN
                                                        val totalTime = time + Utils.getTimeInMinutesFromGraphHopperResponse(responseAsJSON)
                                                        binding.tvResult.text = totalTime.toString()
                                                        binding.mainResultLinearLayout.visibility = View.VISIBLE
                                                    }

                                                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                                        binding.progressBar.visibility = View.GONE
                                                        binding.errorMessageTextView.visibility = View.VISIBLE
                                                    }

                                                })

                                        }

                                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                            binding.progressBar.visibility = View.GONE
                                            binding.errorMessageTextView.visibility = View.VISIBLE
                                        }
                                    })

                            }

                            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                binding.progressBar.visibility = View.GONE
                                binding.errorMessageTextView.visibility = View.VISIBLE
                            }
                        })
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        binding.progressBar.visibility = View.GONE
                        binding.errorMessageTextView.visibility = View.VISIBLE
                    }


                })

        }

        binding.chatbotImageView.setOnClickListener {
            startActivity(Intent(this, ChatbotActivity::class.java))
        }

        binding.helpImageView.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }
    }
}