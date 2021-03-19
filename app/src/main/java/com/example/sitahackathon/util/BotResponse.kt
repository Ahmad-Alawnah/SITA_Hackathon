package com.example.sitahackathon.util

import com.example.sitahackathon.util.Constants.open_google
import com.example.sitahackathon.util.Constants.open_search

object BotResponse {
    fun basicResponses(_message: String):String{
        val random = (0..2).random()
        val m =  _message.toLowerCase()

        return when{
            // hi
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
            m.contains("search") && m.contains("google")->{
                open_search
            }
            else ->
                when(random) {
                    0 -> "I don't understand"
                    1 -> "huh?"
                    2 -> "Try asking me something different"
                    else -> "error"
                }
        }
    }

}