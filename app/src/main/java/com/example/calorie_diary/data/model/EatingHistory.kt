package com.example.calorie_diary.data.model

data class EatingHistory(
    val id: Int?,
    val userId: Int,
    val date: String,
    val foodId: Int,
    val foodWeight: Int
)
