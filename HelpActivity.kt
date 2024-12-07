// HelpActivity.kt

package com.example.a279project

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        // Find the Back Button by its ID
        val backButton: ImageView = findViewById(R.id.backButton)

        // Set OnClickListener for the Back Button to navigate to ProfileActivity
        backButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
