package com.example.calorie_diary.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StringDate {

    private val dateFormat = "yyyy-MM-dd"

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

}