package com.example.calorie_diary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var profileButton: ImageView
    private lateinit var addFoodButton: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        profileButton = findViewById(R.id.profile)
        profileButton.setOnClickListener(this)
        addFoodButton = findViewById(R.id.add_food_btn)
        addFoodButton.setOnClickListener(this)
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
}
