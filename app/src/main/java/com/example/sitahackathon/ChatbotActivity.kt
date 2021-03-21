package com.example.sitahackathon

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sitahackathon.databinding.ActivityChatbotBinding
import com.example.sitahackathon.retrofit.RetrofitServiceFactory
import com.example.sitahackathon.retrofit.Utils
import com.example.sitahackathon.util.BotResponse
import com.example.sitahackathon.util.Constants.get_flight
import com.example.sitahackathon.util.Constants.open_google
import com.example.sitahackathon.util.Constants.open_uber
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ChatbotActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatbotBinding
    val adapter = MessageAdapter(ArrayList<Message>())
    var currentAirLine = ""
    var currentFlightNumber = ""
    val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvMessages.adapter = this.adapter
        val layoutManager = LinearLayoutManager(
                binding.rvMessages.context,
                LinearLayoutManager.VERTICAL,
                false
        )
        binding.rvMessages.layoutManager = layoutManager
        binding.btnSend.setOnClickListener {
            if (binding.etText.toString().trim()!="") {
                binding.etText.error = null
                val message = Message(binding.etText.text.toString().trim(), MessageType.SENT)
                binding.etText.setText("", TextView.BufferType.EDITABLE)
                adapter.addMessage(message)
                if (message.text.toLowerCase().trim().contains("flight")){
                    val separated = message.text.toString().trim().split(' ')
                    try{
                       currentAirLine = separated[1]
                       currentFlightNumber = separated[2]
                    }
                    catch (e: Exception){
                        onBotReply("Please make sure to enter the airline code and the flight number correctly")
                    }

                }
                BotResponse.basicResponses(message.text, this)
            }
            else{
                binding.etText.error = "Text can't be empty"
            }
        }

    }

    fun onBotReply(reply: String){
        adapter.addMessage(Message(reply, MessageType.RECEIVED))
        binding.rvMessages.scrollToPosition(adapter.itemCount - 1)
        when(reply){
            open_google -> {
                val site = Intent(Intent.ACTION_VIEW)
                site.data = Uri.parse("https://www.google.com/")
                executorService.schedule({
                    startActivity(site)
                }, 1, TimeUnit.SECONDS)
            }

            get_flight -> {
                RetrofitServiceFactory.createFlightInfoService().getFlightByAirlineAndNumber(currentAirLine,
                        currentFlightNumber).enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        val responseAsJSON = JSONObject(Gson().toJson(response.body()))
                        if (responseAsJSON["success"].equals(false)) {
                            onBotReply("Flight not found, please make sure you are entering the correct airline code and flight number")
                            return
                        }
                        val estimatedDepartureTime = Utils.getEstimatedDepartureFromRequest(responseAsJSON)
                        var zonedDateTime = Utils.convertStringToZonedDateTime(estimatedDepartureTime)
                        val departureTime = zonedDateTime.toOffsetDateTime().withOffsetSameInstant(OffsetDateTime.now().offset)

                        val estimatedArrivalTime = Utils.getEstimatedArrivalFromRequest(responseAsJSON)
                        zonedDateTime = Utils.convertStringToZonedDateTime(estimatedArrivalTime)
                        val arrivalTime = zonedDateTime.toOffsetDateTime().withOffsetSameInstant(OffsetDateTime.now().offset)

                        onBotReply("Departure: " +
                                DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a").format(departureTime) + "\n" +
                                "Arrival: " + DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a").format(arrivalTime))

                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        onBotReply("Connection issue, please try again")
                    }


                })

            }

            open_uber -> {
                try {
                    //val pm = packageManager.getPackageInfo("com.ubercab", PackageManager.GET_ACTIVITIES)
                    val uri = "uber://?action=setPickup&pickup=my_location";
                    val intent = Intent(Intent.ACTION_VIEW);
                    intent.data = Uri.parse(uri);
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    // Define what your app should do if no activity can handle the intent.
                    Toast.makeText(
                            this,
                            "Uber is not installed on this device",
                            Toast.LENGTH_LONG
                    ).show()

                }
            }

        }

    }

}