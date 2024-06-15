package com.example.calorie_diary.data.model

data class CalorieDiaries(
    val userId: Int?,
    val date: String,
    val progressCalories: Double,
    val maxCalories: Double,
    val progressCarbohydrate: Double,
    val maxCarbohydrate: Double,
    val progressProteins: Double,
    val maxProteins: Double,
    val progressFat: Double,
    val maxFat: Double,
)
