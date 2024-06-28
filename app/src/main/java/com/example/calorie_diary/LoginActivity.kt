package com.example.calorie_diary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calorie_diary.data.DBHelper
import com.example.calorie_diary.util.SystemBar

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var db: DBHelper
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var logInValidationError: TextView
    private lateinit var logInError: TextView
    private lateinit var logInButton: Button
    private lateinit var createAccoutnLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        SystemBar().setSystemBarColor(this)

        db = DBHelper(this, null)
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        logInValidationError = findViewById(R.id.login_validation_error)
        logInError = findViewById(R.id.login_error)
        logInButton = findViewById(R.id.login_button)
        createAccoutnLink = findViewById(R.id.create_account_link)

        createAccoutnLink.setOnClickListener(this)
        logInButton.setOnClickListener(this)
        logInValidationError.visibility = View.GONE
        logInError.visibility = View.GONE

        val userId = db.getCurrentUserId()
        if (userId != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.login_button) {
            logInValidationError.visibility = View.GONE
            logInError.visibility = View.GONE

            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                logInValidationError.visibility = View.VISIBLE
            } else if (!email.matches(android.util.Patterns.EMAIL_ADDRESS.toRegex())) {
                logInValidationError.visibility = View.VISIBLE
            } else {
                logInValidationError.visibility = View.GONE
                if (db.logIn(email, password)) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    logInError.visibility = View.VISIBLE
                }
            }
        } else if (v?.id == R.id.create_account_link) {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}