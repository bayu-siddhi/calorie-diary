package com.example.calorie_diary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calorie_diary.data.DBHelper
import com.example.calorie_diary.data.model.CalorieDiaries

class AddFoodActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var backButton: ImageButton
    private lateinit var foodNameTextView: TextView
    private lateinit var foodCalories100TextView: TextView
    private lateinit var foodCaloriesTextView: TextView
    private lateinit var foodCarbsTextView: TextView
    private lateinit var foodProteinTextView: TextView
    private lateinit var foodFatTextView: TextView
    private lateinit var addFoodButton: Button
    private lateinit var totalCaloriesProgressBar: ProgressBar
    private lateinit var carbsProgressBar: ProgressBar
    private lateinit var proteinProgressBar: ProgressBar
    private lateinit var fatProgressBar: ProgressBar
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_food)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener(this)

        foodNameTextView = findViewById(R.id.foodName)
        foodCalories100TextView = findViewById(R.id.foodCalories)
        foodCaloriesTextView = findViewById(R.id.totalCaloriesText)
        foodCarbsTextView = findViewById(R.id.CarbsText)
        foodProteinTextView = findViewById(R.id.ProteinText)
        foodFatTextView = findViewById(R.id.FatText)
        addFoodButton = findViewById(R.id.addFoodButton)
        addFoodButton.setOnClickListener(this)

        totalCaloriesProgressBar = findViewById(R.id.totalCaloriesProgressBar)
        carbsProgressBar = findViewById(R.id.carbsProgressBar)
        proteinProgressBar = findViewById(R.id.proteinProgressBar)
        fatProgressBar = findViewById(R.id.fatProgressBar)

        dbHelper = DBHelper(this, null)

        val foodName = intent.getStringExtra("foodName")
        val calorieDiaries = dbHelper.getCalorieDiariesByDate(dbHelper.getCurrentUserId()!!, "2024-06-20") // Ganti dengan tanggal yang sesuai
        foodNameTextView.text = foodName

        // Get food data from the database
        val food = dbHelper.getFoodByName(foodName!!)
        if (food != null && calorieDiaries != null) {
            val foodCalories = food.calories * 100
            val foodCarbs = food.carbohydrate * 100
            val foodProtein = food.proteins * 100
            val foodFat = food.fat * 100

            foodCalories100TextView.text = "%.2f cal / 100 g".format(food.calories * 100)
            foodCaloriesTextView.text = "%.2f/%.2f Cal".format(foodCalories, calorieDiaries.maxCalories)
            foodCarbsTextView.text = "%.2f/%.2f g".format(foodCarbs, calorieDiaries.maxCarbohydrate)
            foodProteinTextView.text = "%.2f/%.2f g".format(foodProtein, calorieDiaries.maxProteins)
            foodFatTextView.text = "%.2f/%.2f g".format(foodFat, calorieDiaries.maxFat)

            totalCaloriesProgressBar.max = calorieDiaries.maxCalories.toInt()
            totalCaloriesProgressBar.progress = foodCalories.toInt()
            foodCaloriesTextView.text = buildString {
                append(foodCalories.toInt().toString())
                append("/")
                append(calorieDiaries.maxCalories.toInt().toString())
                append(" Cals")
            }

            carbsProgressBar.max = calorieDiaries.maxCarbohydrate.toInt()
            carbsProgressBar.progress = foodCarbs.toInt()
            foodCarbsTextView.text = buildString {
                append(foodCarbs.toInt().toString())
                append("/")
                append(calorieDiaries.maxCarbohydrate.toInt().toString())
                append(" g")
            }

            proteinProgressBar.max = calorieDiaries.maxProteins.toInt()
            proteinProgressBar.progress = foodProtein.toInt()
            foodProteinTextView.text = buildString {
                append(foodProtein.toInt().toString())
                append("/")
                append(calorieDiaries.maxProteins.toInt().toString())
                append(" g")
            }

            fatProgressBar.max = calorieDiaries.maxFat.toInt()
            fatProgressBar.progress = foodFat.toInt()
            foodFatTextView.text = buildString {
                append(foodFat.toInt().toString())
                append("/")
                append(calorieDiaries.maxFat.toInt().toString())
                append(" g")
            }
        }
    }

    override fun onClick(v: View?) {
        val intent = Intent(this@AddFoodActivity, SearchFoodActivity::class.java)
        startActivity(intent)
    }
}
