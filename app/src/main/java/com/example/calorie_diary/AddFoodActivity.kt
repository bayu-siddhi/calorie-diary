package com.example.calorie_diary

import android.content.ContentValues
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

class AddFoodActivity : AppCompatActivity(), View.OnClickListener  {

    private lateinit var db: DBHelper
    private lateinit var calorieDiaries: CalorieDiaries

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

    private var foodId: Int = 0
    private var foodName: String = ""
    private var foodCalories: Double = 0.0
    private var foodCarbohydrate: Double = 0.0
    private var foodProteins: Double = 0.0
    private var foodFat: Double = 0.0
    private var foodAmount: Int = 0

    private fun updateFoodDetails() {
        // Get food amount (handling empty input)
        foodAmount = if (foodAmountEditText.text.toString().isNotEmpty()) {
            foodAmountEditText.text.toString().toIntOrNull() ?: 0 // Use toIntOrNull() safely
        } else {
            100 // Default to 100 if the EditText is empty
        }

        // Update total calories
        totalCaloriesProgressBar.progress = (foodCalories * foodAmount / 100).toInt()
        totalCaloriesTextView.text = buildString {
            append((foodCalories * foodAmount / 100).toInt().toString())
            append("/")
            append(calorieDiaries.maxCalories.toInt().toString())
            append(" Cals")
        }

        // Update carbs
        carbsProgressBar.progress = (foodCarbohydrate * foodAmount / 100).toInt()
        carbsTextView.text = buildString {
            append((foodCarbohydrate * foodAmount / 100).toInt().toString())
            append("/")
            append(calorieDiaries.maxCarbohydrate.toInt().toString())
            append(" g")
        }

        // Update protein
        proteinProgressBar.progress = (foodProteins * foodAmount / 100).toInt()
        proteinTextView.text = buildString {
            append((foodProteins * foodAmount / 100).toInt().toString())
            append("/")
            append(calorieDiaries.maxProteins.toInt().toString())
            append(" g")
        }

        // Update fat
        fatProgressBar.progress = (foodFat * foodAmount / 100).toInt()
        fatTextView.text = buildString {
            append((foodFat * foodAmount / 100).toInt().toString())
            append("/")
            append(calorieDiaries.maxFat.toInt().toString())
            append(" g")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_food)

        db = DBHelper(this, null)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener(this)

        addFoodButton = findViewById(R.id.addFoodButton)
        addFoodButton.setOnClickListener(this)

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

        // Ambil data food details
//        val foodId = intent.getIntExtra("foodId" 0)
//        val food = db.getFoodById(foodId)

        val foodName = intent.getStringExtra("foodName")
        val food = db.getFoodByName(foodName!!)
        if (food != null) {
            //ngambil foodname, calorie, dsb
            foodCalories = food.calories
            foodCarbohydrate = food.carbohydrate
            foodProteins = food.proteins
            foodFat = food.fat
        }

        // Tampilkan food details
        // Food name
        foodNameTextView.text = foodName
        // Calories 100 gram
        foodCalories *= 100
        foodCaloriesTextView.text = foodCalories.toInt().toString()

        // onchange
        foodAmountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                updateFoodDetails()
            }
        })

        // Update food details (initial values)
        updateFoodDetails()

        // Get Calorie Diaries
        val userId = db.getCurrentUserId() ?: return
        calorieDiaries = db.getCalorieDiariesByDate(userId, StringDate().getCurrentDate()) ?: return

        // Total Calories
        totalCaloriesProgressBar.max = calorieDiaries.maxCalories.toInt()
        totalCaloriesProgressBar.progress = foodCalories.toInt()
        totalCaloriesTextView.text = buildString {
            append(foodCalories.toInt().toString())
            append("/")
            append(calorieDiaries.maxCalories.toInt().toString())
            append(" Cals")
        }
        // Carbs
        carbsProgressBar.max = calorieDiaries.maxCarbohydrate.toInt()
        foodCarbohydrate *= 100
        carbsProgressBar.progress = foodCarbohydrate.toInt()
        carbsTextView.text = buildString {
            append(foodCarbohydrate.toInt().toString())
            append("/")
            append(calorieDiaries.maxCarbohydrate.toInt().toString())
            append(" g")
        }
        // Protein
        proteinProgressBar.max = calorieDiaries.maxProteins.toInt()
        foodProteins *= 100
        proteinProgressBar.progress = foodProteins.toInt()
        proteinTextView.text = buildString {
            append(foodProteins.toInt().toString())
            append("/")
            append(calorieDiaries.maxProteins.toInt().toString())
            append(" g")
        }
        // Fat
        fatProgressBar.max = calorieDiaries.maxFat.toInt()
        foodFat *= 100
        fatProgressBar.progress = foodFat.toInt()
        fatTextView.text = buildString {
            append(foodFat.toInt().toString())
            append("/")
            append(calorieDiaries.maxFat.toInt().toString())
            append(" g")
        }
        // foodCaloriesTextView.text = getText(R.string.food_calories).toString().replace("93", foodCalories.toInt().toString())
    }

    override fun onClick(v: View?) {
//        val intent = Intent(this@AddFoodActivity, SearchFoodActivity::class.java)
//        startActivity(intent)
        // lihat di main juga if(v?)
        if (v?.id == R.id.backButton) {
            val intent = Intent(this@AddFoodActivity, SearchFoodActivity::class.java)
            startActivity(intent)
        } else if (v?.id == R.id.addFoodButton) {
            // Get food amount
            foodAmount = foodAmountEditText.text.toString().toIntOrNull() ?: 0

            // Add food to eating history
            addFoodToEatingHistory()

            val intent = Intent(this@AddFoodActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addFoodToEatingHistory() {
        // Get user id
        val userId = db.getCurrentUserId() ?: return

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
            foodCalories,
            foodCarbohydrate,
            foodProteins,
            foodFat
        )
    }
}
