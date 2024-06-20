package com.example.calorie_diary

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calorie_diary.data.DBHelper
import com.example.calorie_diary.data.model.CalorieDiaries
import com.example.calorie_diary.data.model.EatingHistory
import com.example.calorie_diary.util.StringDate
import com.example.calorie_diary.util.StringFunction

class AddFoodActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var db: DBHelper

    private lateinit var foodNameTextView: TextView
    private lateinit var foodCaloriesTextView: TextView
    private lateinit var foodAmountEditText: EditText
    private lateinit var addFoodButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var totalCaloriesTextView: TextView
    private lateinit var totalCaloriesProgressBar: ProgressBar
    private lateinit var carbsTextView: TextView
    private lateinit var carbsProgressBar: ProgressBar
    private lateinit var proteinTextView: TextView
    private lateinit var proteinProgressBar: ProgressBar
    private lateinit var fatTextView: TextView
    private lateinit var fatProgressBar: ProgressBar

    private var foodName: String = ""
    private var foodCalories: Double = 0.0
    private var foodCarbohydrate: Double = 0.0
    private var foodProteins: Double = 0.0
    private var foodFat: Double = 0.0
    private var foodAmount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_food)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = DBHelper(this, null)

        foodNameTextView = findViewById(R.id.foodName)
        foodCaloriesTextView = findViewById(R.id.foodCalories)
        foodAmountEditText = findViewById(R.id.foodAmount)
        totalCaloriesTextView = findViewById(R.id.totalCaloriesText)
        totalCaloriesProgressBar = findViewById(R.id.totalCaloriesProgressBar)
        carbsTextView = findViewById(R.id.CarbsText)
        carbsProgressBar = findViewById(R.id.carbsProgressBar)
        proteinTextView = findViewById(R.id.ProteinText)
        proteinProgressBar = findViewById(R.id.proteinProgressBar)
        fatTextView = findViewById(R.id.FatText)
        fatProgressBar = findViewById(R.id.fatProgressBar)

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener(this)

        addFoodButton = findViewById(R.id.addFoodButton)
        addFoodButton.setOnClickListener(this)

        // Ambil data food details
        val foodId = intent.getIntExtra("foodId", 0)
        val food = db.getFoodById(foodId)
        if (food != null) {
            foodName = food.name
            foodCalories = food.calories
            foodCarbohydrate = food.carbohydrate
            foodProteins = food.proteins
            foodFat = food.fat

            // Tampilkan food details
            foodNameTextView.text = StringFunction().titleCase((foodName))
            foodCaloriesTextView.text = buildString {
                append(foodCalories.times(100).toInt().toString())
                append(" cals / 100 g")
            }

            updateFoodDetails()

        } else {
            val intent = Intent(this@AddFoodActivity, SearchFoodActivity::class.java)
            startActivity(intent)
            finish()
        }

        foodAmountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateFoodDetails()
            }
        })
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.backButton) {
            val intent = Intent(this@AddFoodActivity, SearchFoodActivity::class.java)
            startActivity(intent)
        } else if (v?.id == R.id.addFoodButton) {
            addFoodToEatingHistory()
            val intent = Intent(this@AddFoodActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateFoodDetails() {
        // Get food amount (handling empty input)
        foodAmount = if (foodAmountEditText.text.toString().isNotEmpty()) {
            foodAmountEditText.text.toString().toIntOrNull() ?: 0
        } else {
            100 // Default
        }

        // Get Calorie Diaries
        val userId = db.getCurrentUserId()
        if (userId != null) {
            val calorieDiaries: CalorieDiaries? = db.getCalorieDiariesByDate(userId, StringDate().getCurrentDate())
            if (calorieDiaries !== null) {
                // Total Calories
                totalCaloriesProgressBar.max = calorieDiaries.maxCalories.toInt()
                totalCaloriesProgressBar.progress = foodCalories.times(foodAmount).toInt()
                totalCaloriesTextView.text = buildString {
                    append(foodCalories.times(foodAmount).toInt().toString())
                    append("/")
                    append(calorieDiaries.maxCalories.toInt().toString())
                    append(" Cals")
                }
                // Carbs
                carbsProgressBar.max = calorieDiaries.maxCarbohydrate.toInt()
                carbsProgressBar.progress = foodCarbohydrate.times(foodAmount).toInt()
                carbsTextView.text = buildString {
                    append(foodCarbohydrate.times(foodAmount).toInt().toString())
                    append("/")
                    append(calorieDiaries.maxCarbohydrate.toInt().toString())
                    append(" g")
                }
                // Protein
                proteinProgressBar.max = calorieDiaries.maxProteins.toInt()
                proteinProgressBar.progress = foodProteins.times(foodAmount).toInt()
                proteinTextView.text = buildString {
                    append(foodProteins.times(foodAmount).toInt().toString())
                    append("/")
                    append(calorieDiaries.maxProteins.toInt().toString())
                    append(" g")
                }
                // Fat
                fatProgressBar.max = calorieDiaries.maxFat.toInt()
                fatProgressBar.progress = foodFat.times(foodAmount).toInt()
                fatTextView.text = buildString {
                    append(foodFat.times(foodAmount).toInt().toString())
                    append("/")
                    append(calorieDiaries.maxFat.toInt().toString())
                    append(" g")
                }
            } else {
                val intent = Intent(this@AddFoodActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            val intent = Intent(this@AddFoodActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun addFoodToEatingHistory() {
        foodAmount = if (foodAmountEditText.text.toString().isNotEmpty()) {
            foodAmountEditText.text.toString().toIntOrNull() ?: 0
        } else {
            100 // Default
        }
        val userId = db.getCurrentUserId()
        val foodId = intent.getIntExtra("foodId", 0)
        if (userId != null) {
            // Add eating history
            val eatingHistory = EatingHistory(
                null,
                userId,
                StringDate().getCurrentDate(),
                foodId,
                foodAmount
            )

            db.addEatingHistory(eatingHistory)

            // Update calorie diaries
            db.updateCalorieDiariesProgress(
                userId,
                StringDate().getCurrentDate(),
                foodCalories.times(foodAmount),
                foodCarbohydrate.times(foodAmount),
                foodProteins.times(foodAmount),
                foodFat.times(foodAmount)
            )
        } else {
            val intent = Intent(this@AddFoodActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
