package com.example.calorie_diary.data.model

data class CalorieDiaries(
    val userId: Int?,
    val date: String,
    val progressCalories: Double,
    var maxCalories: Double,
    val progressCarbohydrate: Double,
    var maxCarbohydrate: Double,
    val progressProteins: Double,
    var maxProteins: Double,
    val progressFat: Double,
    var maxFat: Double,
)
