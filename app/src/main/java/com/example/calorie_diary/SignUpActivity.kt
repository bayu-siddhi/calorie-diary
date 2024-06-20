package com.example.calorie_diary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calorie_diary.data.DBHelper
import com.example.calorie_diary.data.model.User
import com.example.calorie_diary.util.SystemBar


class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var db: DBHelper
    private lateinit var user: User

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var weightInput: EditText
    private lateinit var heightInput: EditText
    private lateinit var maleGenderInput: RadioButton
    private lateinit var femaleGenderInput: RadioButton
    private lateinit var passwordInput: EditText
    private lateinit var signUpButton: Button
    private lateinit var signUpError: TextView
    private lateinit var emailExistsError: TextView
    private lateinit var signInLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        SystemBar().setSystemBarColor(this)

        db = DBHelper(this, null)
        nameInput = findViewById(R.id.name_input)
        emailInput = findViewById(R.id.email_input)
        ageInput = findViewById(R.id.age_input)
        weightInput = findViewById(R.id.weight_input)
        heightInput = findViewById(R.id.height_input)
        maleGenderInput = findViewById(R.id.male_rdb)
        femaleGenderInput = findViewById(R.id.female_rdb)
        passwordInput = findViewById(R.id.password_input)
        signUpButton = findViewById(R.id.signup_button)
        signUpError = findViewById(R.id.signup_error)
        emailExistsError = findViewById(R.id.email_exists_error)
        signInLink = findViewById(R.id.signin_link)

        signInLink.setOnClickListener(this)
        signUpButton.setOnClickListener(this)
        signUpError.visibility = View.GONE
        emailExistsError.visibility = View.GONE

        val userId = db.getCurrentUserId()
        if (userId != null) {
            val intent = Intent(this@SignUpActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.signup_button) {
            signUpError.visibility = View.GONE
            emailExistsError.visibility = View.GONE

            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val age = ageInput.text.toString().trim()
            val weight = weightInput.text.toString().trim()
            val height = heightInput.text.toString().trim()

            var gender = ""
            if (maleGenderInput.isChecked) {
                gender = "M"
            } else if (femaleGenderInput.isChecked) {
                gender = "F"
            }

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() ||
                age.isEmpty() ||weight.isEmpty() || height.isEmpty() || gender.isEmpty()) {
                signUpError.visibility = View.VISIBLE
            } else if (!email.matches(android.util.Patterns.EMAIL_ADDRESS.toRegex())) {
                signUpError.visibility = View.VISIBLE
            } else if (age.toInt() <= 0 || weight.toInt() <= 0 || height.toInt() <= 0) {
                signUpError.visibility = View.VISIBLE
            } else {
                signUpError.visibility = View.GONE
                user = User(null, name, email, password, age.toInt(),
                    weight.toInt(), height.toInt(), gender, 0
                )
                if (db.signUp(user)) {
                    val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    emailExistsError.visibility = View.VISIBLE
                }
            }
        } else if (v?.id == R.id.signin_link) {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}