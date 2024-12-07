// EditProfileActivity.kt

package com.example.a279project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile) // Ensure this matches your edit profile layout

        // Find the Cancel and Save buttons by their IDs
        val cancelButton: Button = findViewById(R.id.cancelButton)
        val saveButton: Button = findViewById(R.id.saveButton)

        // Set an OnClickListener on the Cancel button to go back to ProfileActivity
        cancelButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish() // Finish the current activity to prevent going back to it
        }

        // Set an OnClickListener on the Save button to go back to ProfileActivity
        saveButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish() // Finish the current activity to prevent going back to it
        }
    }
}
