package com.example.calorie_diary.util

class Calculator(
    val gender: String,
    val weight: Int,
    val height: Int,
    val age: Int
) {

    private val carbohydratePercentage = 0.5
    private val proteinsPercentage = 0.3
    private val fatPercentage = 0.2

    fun maxCalories(): Double {
        val maxCalories: Double
        if (gender == "M") {
            maxCalories = 1.2 * ((10 * weight) + (6.5 * height) - (5 * age) + 5)
        } else {
            maxCalories = 1.2 * ((10 * weight) + (6.5 * height) - (5 * age) - 161)
        }
        return maxCalories
    }

    fun maxCarbohydrate(): Double {
        return (carbohydratePercentage * maxCalories()) / 4
    }

    fun maxProteins(): Double {
        return (proteinsPercentage * maxCalories()) / 4
    }

    fun maxFat(): Double {
        return (fatPercentage * maxCalories()) / 9
    }

}