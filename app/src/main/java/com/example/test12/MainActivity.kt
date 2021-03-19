package com.example.test12

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.test12.databinding.ActivityMainBinding
import com.example.test12.retrofit.RetrofitServiceFactory
import com.example.test12.retrofit.Utils
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter



class MainActivity : AppCompatActivity(){

    lateinit var binding: ActivityMainBinding
    lateinit var userLocation: LatLng
    lateinit var fusedLocationClient: FusedLocationProviderClient
    val PERMISSION_REQUEST_CODE = 12
    lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        getCurrentLocation()

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
                        var offsetDateTime = zonedDateTime.toOffsetDateTime().withOffsetSameInstant(
                                OffsetDateTime.now().offset
                        )
                        val departureTime = zonedDateTime.toOffsetDateTime().withOffsetSameInstant(
                                OffsetDateTime.now().offset
                        )

                        RetrofitServiceFactory.createAirportService().getAirportInfo(departureAirportCode).enqueue(object : Callback<JsonObject> {

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

                                RetrofitServiceFactory.createWaitTimeService().getWaitTimesForAirport(departureAirportCode)
                                        .enqueue(object : Callback<JsonObject> {
                                            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                                                val responseAsJSON = JSONObject(Gson().toJson(response.body()))
                                                val time = Utils.getSumOfLongestCheckpointsOrBufferTimeInMinutes(responseAsJSON)

                                                val points = ArrayList<String>()
                                                points.add(userLocation.latitude.toString()+","+userLocation.longitude)
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
                                                                val totalTime = time + Utils.getTimeInMinutesFromGraphHopperResponse(responseAsJSON) + 30
                                                                offsetDateTime = offsetDateTime.minusMinutes(totalTime.toLong())
                                                                binding.tvLeaveTime.text =
                                                                        DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a").format(
                                                                                offsetDateTime
                                                                        )

                                                                if (OffsetDateTime.now().isAfter(departureTime)) {
                                                                    //TODO: Print that the they missed the flight
                                                                } else if (OffsetDateTime.now().isAfter(offsetDateTime)) {
                                                                    //TODO: Hurry up
                                                                }

                                                                binding.tvTotalTime.text = (totalTime / 60).toString() + "hours and " +
                                                                        totalTime % 60 + " minutes"

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


    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(){
        if (isLocationPermissionGranted()){

            if (isLocationAndInternetEnabled()) {


                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    val location = task.result
                    if (location == null){
                        requestLocation()
                    }
                    else{
                        userLocation = LatLng(location.latitude, location.longitude)
                        //Toast.makeText(applicationContext, userLocation.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            else {
                //TODO: Ask the user to turn on their location and their internet
                //If you wanna be fancy, take them to the settings
            }
        }
        else{
            //TODO: if location permission is denied, display an note indicating that the application can't be used
            requestLocationPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    fun requestLocation(){
        val locationRequest = LocationRequest()
        locationRequest.interval = 5
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.numUpdates = 1

        fusedLocationClient.requestLocationUpdates(locationRequest,
        object: LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                var maxAccuracy: Float = -1.0f
                var index = -1
                for(i in 0 until p0.locations.size){
                    if (p0.locations[i].accuracy > maxAccuracy){
                        maxAccuracy = p0.locations[i].accuracy
                        index = i
                    }
                }
                userLocation = LatLng(p0.locations[index].latitude, p0.locations[index].longitude)
                //Toast.makeText(applicationContext, userLocation.toString(), Toast.LENGTH_SHORT).show()
            }
        }, Looper.myLooper()!!)

    }
    

    private fun isLocationPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationAndInternetEnabled():Boolean{
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private fun requestLocationPermissions(){
        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation()
            }
            else{
                //TODO: Display the same error for not granting permissions
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getCurrentLocation()

    }


}