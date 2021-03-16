package com.example.test12.retrofit

import org.json.JSONArray
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


        fun getLatitudeAndLongitude(response: JSONObject): Pair<Double,Double>{
//            if (!response.has("airports")){ errors should be checked via status codes in the callbacks and not here
//                return Pair(0.0,0.0)
//            }
            val airport = response.getJSONArray("airports").getJSONObject(0)
            return Pair(airport.getDouble("latitude"), airport.getDouble("longitude"))
        }


        //TODO: NEEDS TESTING
        fun getDurationFromGoogleDestinationResponse(response: JSONObject): Int{
            //In case each route had a duration object (or duration in traffic)
            val routes = response.getJSONArray("routes")
            var mx = -1
            /*
            for(i in 0 until routes.length()){
                if (routes.getJSONObject(i).has("duration_in_traffic")){
                    if (routes.getJSONObject(i).getJSONObject("duration_in_traffic").getInt("value")>mx){
                        mx = routes.getJSONObject(i).getJSONObject("duration_in_traffic").getInt("value")
                    }
                }
                else{
                    if (routes.getJSONObject(i).getJSONObject("duration").getInt("value")>mx){
                        mx = routes.getJSONObject(i).getJSONObject("duration").getInt("value")
                    }
                }

            }
             */

            //in case route objects don't have duration
            for(i in 0 until routes.length()){
                var sum = 0
                val legs = routes.getJSONObject(i).getJSONArray("legs")
                for(j in 0 until legs.length()){
                    val steps = legs.getJSONObject(j).getJSONArray("steps")
                    for(k in 0 until steps.length()){
                        sum += steps.getJSONObject(k).getJSONObject("duration").getInt("value")
                    }
                }
                if (sum > mx) mx = sum
            }

            return mx/60
        }
    }
}