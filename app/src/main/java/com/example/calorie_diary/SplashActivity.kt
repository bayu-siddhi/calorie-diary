package com.example.calorie_diary

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calorie_diary.data.DBHelper
import com.example.calorie_diary.data.source.FoodSource

class SplashActivity : AppCompatActivity() {

    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = DBHelper(this, null)

        val userId = db.getCurrentUserId()
        if (userId == null) {
            addFoodData()
            Handler().postDelayed({
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)
        } else {
            Handler().postDelayed({
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)
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