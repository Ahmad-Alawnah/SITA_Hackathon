package com.example.test12.retrofit

import org.json.JSONObject
import kotlin.collections.ArrayList

class Utils {

    companion object{
        fun getEstimatedDepartureFromRequest(response: JSONObject): String =
            response.getJSONArray("flightRecords").getJSONObject(0)
                .getJSONObject("departure").getString("estimated")


        fun getSumOfLongestCheckpointsOrBufferTimeInMinutes(response: JSONObject):Int{
            if (!response.has("current")){
                return 180 //3 hours in case no details are available about checkpoints in the airport (may change)
            }
            val data = ArrayList<Int>()
            val length = response.getJSONArray("current").length()
            for(i in 0 until length){
                data.add(response.getJSONArray("current").getJSONObject(i).getInt("projectedMaxWaitMinutes"))
            }
            data.sort()
            if (length>=3)
                return data[length-1] + data[length-2] + data[length-3]
            else{
                var sum = 0
                for(i in 0 until length)sum+=data[i]
                return sum
            }

        }

        fun getDepartureAirportCode(response: JSONObject) =
            response.getJSONArray("flightRecords").getJSONObject(0)
                .getJSONObject("departure").getJSONObject("airport").getString("iataCode")
    }
}