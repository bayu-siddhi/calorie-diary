package com.example.calorie_diary

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calorie_diary.data.DBHelper
import com.example.calorie_diary.data.source.FoodSource
import com.example.calorie_diary.data.model.CalorieDiaries
import com.example.calorie_diary.data.model.TodayEatingHistory
import com.example.calorie_diary.data.model.User
import com.example.calorie_diary.util.Calculator
import com.example.calorie_diary.util.StringDate
import com.example.calorie_diary.util.SystemBar

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var db: DBHelper
    private lateinit var calculator: Calculator
    private lateinit var stringDate: StringDate

    private lateinit var profileButton: ImageView
    private lateinit var addFoodButton: LinearLayout
    private lateinit var caloriesProgress: ProgressBar
    private lateinit var caloriesText: TextView
    private lateinit var carbohydrateProgress: ProgressBar
    private lateinit var carbohydrateText: TextView
    private lateinit var proteinsProgress: ProgressBar
    private lateinit var proteinsText: TextView
    private lateinit var fatProgress: ProgressBar
    private lateinit var fatText: TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var todayEatingHistoryArrayList: ArrayList<TodayEatingHistory>
    private lateinit var todayEatingHistoryAdapter: TodayEatingHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        SystemBar().setSystemBarColor(this)

        db = DBHelper(this, null)
        profileButton = findViewById(R.id.profile)
        addFoodButton = findViewById(R.id.add_food_btn)
        profileButton.setOnClickListener(this)
        addFoodButton.setOnClickListener(this)

        caloriesProgress = findViewById(R.id.progress_cals)
        carbohydrateProgress = findViewById(R.id.progress_carbs)
        proteinsProgress = findViewById(R.id.progress_protein)
        fatProgress = findViewById(R.id.progress_fat)

        caloriesText = findViewById(R.id.cals_text)
        carbohydrateText = findViewById(R.id.carbs_text)
        proteinsText = findViewById(R.id.protein_text)
        fatText = findViewById(R.id.fat_text)

        val userId: Int? = db.getCurrentUserId()
        if (userId != null) {
            startCaloriDiariesToday(userId)
            showCalorieDiariesToday(userId)
            showEatingHistoryToday(userId)
            addFoodData()
        } else {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.profile) {
            val intent = Intent(this@MainActivity, EditProfileActivity::class.java)
            startActivity(intent)
        } else if (v?.id == R.id.add_food_btn) {
            val intent = Intent(this@MainActivity, SearchFoodActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startCaloriDiariesToday(userId: Int) {
        stringDate = StringDate()
        var calorieDiaries: CalorieDiaries? =
            db.getCalorieDiariesByDate(userId, stringDate.getCurrentDate())
        if (calorieDiaries == null) {
            val user: User? = db.getUserById(userId)
            if (user != null) {
                calculator = Calculator(user.gender, user.weight, user.height, user.age)
                calorieDiaries = CalorieDiaries(
                    userId, stringDate.getCurrentDate(),
                    0.0, calculator.maxCalories(),
                    0.0, calculator.maxCarbohydrate(),
                    0.0, calculator.maxProteins(),
                    0.0, calculator.maxFat()
                )
                db.addCalorieDiaries(calorieDiaries)
            } else {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showCalorieDiariesToday(userId: Int) {
        stringDate = StringDate()
        var calorieDiaries: CalorieDiaries? =
            db.getCalorieDiariesByDate(userId, stringDate.getCurrentDate())
        if (calorieDiaries != null) {
            // Calories
            caloriesProgress.max = calorieDiaries.maxCalories.toInt()
            caloriesProgress.progress = calorieDiaries.progressCalories.toInt()
            caloriesText.text = buildString {
                append(calorieDiaries.progressCalories.toInt().toString())
                append("/")
                append(calorieDiaries.maxCalories.toInt().toString())
                append("\ncals")
            }
            // Carbohydrate
            carbohydrateProgress.max = calorieDiaries.maxCarbohydrate.toInt()
            carbohydrateProgress.progress = calorieDiaries.progressCarbohydrate.toInt()
            carbohydrateText.text = buildString {
                append(calorieDiaries.progressCarbohydrate.toInt().toString())
                append("/")
                append(calorieDiaries.maxCarbohydrate.toInt().toString())
                append(" g")
            }
            // Proteins
            proteinsProgress.max = calorieDiaries.maxProteins.toInt()
            proteinsProgress.progress = calorieDiaries.progressProteins.toInt()
            proteinsText.text = buildString {
                append(calorieDiaries.progressProteins.toInt().toString())
                append("/")
                append(calorieDiaries.maxProteins.toInt().toString())
                append(" g")
            }
            // Fat
            fatProgress.max = calorieDiaries.maxFat.toInt()
            fatProgress.progress = calorieDiaries.progressFat.toInt()
            fatText.text = buildString {
                append(calorieDiaries.progressFat.toInt().toString())
                append("/")
                append(calorieDiaries.maxFat.toInt().toString())
                append(" g")
            }

            val sharedPreferences = getSharedPreferences("calorie_data", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putFloat("totalCalories", calorieDiaries.progressCalories.toFloat())
            editor.putFloat("maxCalories", calorieDiaries.maxCalories.toFloat())
            editor.apply()

        } else {
            startCaloriDiariesToday(userId)
        }
    }

    private fun showEatingHistoryToday(userId: Int) {
        stringDate = StringDate()
        recyclerView = findViewById(R.id.today_eating_history_recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        todayEatingHistoryArrayList = db.getTodayEatingHistoryByDate(userId, stringDate.getCurrentDate())
        // Data Dummy
        // if (todayEatingHistoryArrayList.size == 0) {
        //     db.addEatingHistory(EatingHistory(1, 1, stringDate.getCurrentDate(), 1, 100))
        //     db.addEatingHistory(EatingHistory(2, 1, stringDate.getCurrentDate(), 2, 200))
        //     db.addEatingHistory(EatingHistory(3, 1, stringDate.getCurrentDate(), 3, 300))
        //     db.addEatingHistory(EatingHistory(4, 1, stringDate.getCurrentDate(), 4, 300))
        //     db.addEatingHistory(EatingHistory(5, 1, stringDate.getCurrentDate(), 5, 300))
        //     db.addEatingHistory(EatingHistory(6, 1, stringDate.getCurrentDate(), 6, 300))
        // }
        todayEatingHistoryAdapter = TodayEatingHistoryAdapter(todayEatingHistoryArrayList)
        recyclerView.adapter = todayEatingHistoryAdapter

        todayEatingHistoryAdapter.onItemClick = {
            db.deleteEatingHistoryById(it.id, it.foodId, userId)
            showCalorieDiariesToday(userId)
            showEatingHistoryToday(userId)
        }
    }

    private fun addFoodData() {
        if (db.getAllFood().size == 0) {
            val foodSource = FoodSource()
            val foodArrayList = foodSource.getFoodArrayList()
            for (food in foodArrayList) {
                db.addFood(food)
            }
        }
    }

}

class TodayEatingHistoryAdapter(
    private val todayEatingHistoryArrayList: ArrayList<TodayEatingHistory>
): RecyclerView.Adapter<TodayEatingHistoryAdapter.TodayEatingHistoryViewHolder>() {

    var onItemClick: ((TodayEatingHistory) -> Unit)? = null

    class TodayEatingHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eatingHistoryText: TextView = itemView.findViewById(R.id.today_eating_history_text)
        val delete: ImageView = itemView.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayEatingHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_today_eating_history,
            parent, false)
        return TodayEatingHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return todayEatingHistoryArrayList.size
    }

    override fun onBindViewHolder(holder: TodayEatingHistoryViewHolder, position: Int) {
        val todayEatingHistory = todayEatingHistoryArrayList[position]
        holder.eatingHistoryText.text = buildString {
            append(todayEatingHistory.name)
            append(" (")
            append(todayEatingHistory.foodWeight.toString())
            append(" g)")
        }
        holder.delete.setOnClickListener {
            onItemClick?.invoke(todayEatingHistory)
        }
    }
}
