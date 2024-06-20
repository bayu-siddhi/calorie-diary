package com.example.calorie_diary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calorie_diary.data.DBHelper
import com.example.calorie_diary.util.SystemBar

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var db: DBHelper
    private lateinit var backButton: ImageButton
    private lateinit var logOutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        SystemBar().setSystemBarColor(this)

        db = DBHelper(this, null)
        backButton = findViewById(R.id.backButton)
        logOutButton = findViewById(R.id.logout_button)

        backButton.setOnClickListener(this)
        logOutButton.setOnClickListener(this)

        val userId = db.getCurrentUserId()
        if (userId == null) {
            val intent = Intent(this@EditProfileActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.logout_button) {
            val userId = db.getCurrentUserId()
            if (userId != null) {
                db.logout(userId)
            }
            val intent = Intent(this@EditProfileActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else if (v?.id == R.id.backButton) {
            val intent = Intent(this@EditProfileActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}