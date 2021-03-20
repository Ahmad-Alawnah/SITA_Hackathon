package com.example.sitahackathon.util

import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
object Time {
    fun timeStamp():String{
        val currentDateTime = LocalDateTime.now()
        return currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }
}