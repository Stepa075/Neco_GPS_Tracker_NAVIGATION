package com.stepa_0751.neco_gps_tracker.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone
@SuppressLint("SimpleDateFormat")
object TimeUtils {

    // функция для перевода времени в адекватный вид и отсчета таймера
    private val timeFormatter = SimpleDateFormat("HH:mm:ss")
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm")
    fun getTime(timeInMillis: Long): String{
        val cv = Calendar.getInstance()
        timeFormatter.timeZone = TimeZone.getTimeZone(("UTC"))

        cv.timeInMillis = timeInMillis
        return timeFormatter.format(cv.time)
    }

    fun getDate(): String{
        val cv = Calendar.getInstance()
        return dateFormatter.format(cv.time)
    }
}