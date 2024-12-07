package com.example.a279project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Find the Continue button
        val continueButton: Button = findViewById(R.id.continueButton)

        // Set an OnClickListener to navigate back to LoginActivity
        continueButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Optional: Closes the ForgotPasswordActivity so the user can't return to it using the back button
        }

        // Find the Back button
        val backButton: ImageView = findViewById(R.id.backButton)

        // Set an OnClickListener for the back button to navigate back to LoginActivity
        backButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close ForgotPasswordActivity
        }
    }
}
