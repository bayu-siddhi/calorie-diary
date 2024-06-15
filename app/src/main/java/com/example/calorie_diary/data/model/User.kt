package com.example.calorie_diary.data.model

data class User(
    val id: Int?,
    val name: String,
    val email: String,
    val password: String?,
    val age: Int,
    val weight: Int,
    val height: Int,
    val gender: String,
    val isLoggedIn: Int
)
