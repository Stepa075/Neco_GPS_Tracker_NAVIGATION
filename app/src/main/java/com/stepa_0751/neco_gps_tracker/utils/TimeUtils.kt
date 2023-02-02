package com.stepa_0751.neco_gps_tracker.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

object TimeUtils {

    // функция для перевода времени в адекватный вид и отсчета таймера
    @SuppressLint("SimpleDateFormat")
    private val timeFormatter = SimpleDateFormat("HH:mm:ss")
    fun getTime(timeInMillis: Long): String{
        val cv = Calendar.getInstance()
        timeFormatter.timeZone = TimeZone.getTimeZone(("UTC"))

        cv.timeInMillis = timeInMillis
        return timeFormatter.format(cv.time)
    }
}