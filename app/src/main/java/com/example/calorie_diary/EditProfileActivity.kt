package com.example.calorie_diary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calorie_diary.data.DBHelper
import com.example.calorie_diary.data.model.User
import com.example.calorie_diary.util.Calculator
import com.example.calorie_diary.util.StringDate

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var db: DBHelper
    private lateinit var user: User
    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var weightInput: EditText
    private lateinit var heightInput: EditText
    private lateinit var maleGenderInput: RadioButton
    private lateinit var femaleGenderInput: RadioButton
    private lateinit var backButton: ImageButton
    private lateinit var saveChangesButton: Button
    private lateinit var calculator: Calculator
    private lateinit var stringDate: StringDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = DBHelper(this, null)

        // Inisialisasi input field
        nameInput = findViewById(R.id.name_edit)
        emailInput = findViewById(R.id.email_edit)
        ageInput = findViewById(R.id.age_edit)
        weightInput = findViewById(R.id.weight_edit)
        heightInput = findViewById(R.id.height_edit)
        maleGenderInput = findViewById(R.id.rdb_male_edit)
        femaleGenderInput = findViewById(R.id.rdb_female_edit)
        backButton = findViewById(R.id.backButton)
        saveChangesButton = findViewById(R.id.save_changes_button)

        // Ambil data pengguna dari database berdasarkan ID pengguna yang sedang login
        val userId = db.getCurrentUserId()
        if (userId != null) {
            user = db.getUserById(userId)!!

            // Tampilkan data pengguna pada EditText di layout
            nameInput.setText(user.name)
            emailInput.setText(user.email)
            ageInput.setText(user.age.toString())
            weightInput.setText(user.weight.toString())
            heightInput.setText(user.height.toString())

            // Tentukan gender berdasarkan data pengguna
            if (user.gender == "M") {
                maleGenderInput.isChecked = true
            } else {
                femaleGenderInput.isChecked = true
            }
        }
        saveChangesButton.setOnClickListener {
            saveChanges()
        }
        backButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val intent = Intent(this@EditProfileActivity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun saveChanges() {
        val newName = nameInput.text.toString()
        val newAge = ageInput.text.toString().toInt()
        val newWeight = weightInput.text.toString().toInt()
        val newHeight = heightInput.text.toString().toInt()
        val newGender = if (maleGenderInput.isChecked) "M" else "F"

        // Save updated user to database
        val userId = user.id
        if (userId != null) {
            db.updateUser(userId, newName, newAge, newWeight, newHeight, newGender)

           // Update Calculator with new user data
            calculator = Calculator(newGender, newWeight, newHeight, newAge)

            // Update calorie diaries in MainActivity
            val currentDate = StringDate().getCurrentDate()
            db.updateCalorieDiariesMax(
                userId, currentDate,
                calculator.maxCalories(),
                calculator.maxCarbohydrate(),
                calculator.maxProteins(),
                calculator.maxFat()
            )

            // Navigate back to the main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Optional: finish current activity to prevent going back to it
        } else {
            Toast.makeText(this, "Failed to save changes", Toast.LENGTH_SHORT).show()
        }
    }


}
