package com.example.sitahackathon.util

import com.example.sitahackathon.ChatbotActivity
import com.example.sitahackathon.util.Constants.open_google
import com.example.sitahackathon.util.Constants.get_flight
import com.example.sitahackathon.util.Constants.open_uber


object BotResponse {
    fun basicResponses(_message: String, callingActivity: ChatbotActivity){
        val random = (0..2).random()
        val m =  _message.toLowerCase().trim()
        callingActivity.onBotReply(when{
            m.contains("hi")->{
                when(random) {
                    0 -> "Hello there !!"
                    1-> "HIIIII"
                    2 -> "Hey"
                    else -> "error"
                }
                }
            m.contains("how are you")->{
                when(random) {
                    0 -> "Good"
                    1-> "I'm fine"
                    2 -> "I am hungry"
                    else -> "error"
                }
            }
            m.contains("open") && m.contains("google")->{
                open_google
            }
            m.contains("flight") && m.length > 6->{
                get_flight
            }
            m.contains("time") && m.contains("now")->{
                val t = Time.timeStamp()
                t.toString()
            }
            m.contains("uber")->{
                open_uber
            }

            else ->
                when(random) {
                    0 -> "I don't understand"
                    1 -> "huh?"
                    2 -> "Try asking me something different"
                    else -> "error"
                }
        })
    }

}