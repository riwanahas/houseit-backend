package com.example.a279project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile) // Ensure this matches your profile layout

        // Initialize Firebase Authentication and DatabaseHelper
        firebaseAuth = FirebaseAuth.getInstance()
        dbHelper = DatabaseHelper(this)

        // Get current user ID
        val userId = firebaseAuth.currentUser?.uid

        // Fetch and display the user's full name
        val fullNameTextView: TextView = findViewById(R.id.profileName)
        if (userId != null) {
            val fullName = dbHelper.getUserFullName(userId)
            if (!fullName.isNullOrEmpty()) {
                fullNameTextView.text = fullName
            } else {
                fullNameTextView.text = "Full Name Not Found"
                Toast.makeText(this, "Could not retrieve user information.", Toast.LENGTH_SHORT).show()
            }
        } else {
            fullNameTextView.text = "Not Logged In"
        }

        // Edit Profile button
        val editProfileButton: Button = findViewById(R.id.editProfileButton)
        editProfileButton.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        // Search icon
        val searchIcon: ImageView = findViewById(R.id.searchIcon)
        searchIcon.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        // Help option
        val helpOption: LinearLayout = findViewById(R.id.helpOption)
        helpOption.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)
        }

        // Logout functionality
        val logoutTextView: TextView = findViewById(R.id.logout)
        logoutTextView.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Saved icon
        val savedIcon: ImageView = findViewById(R.id.savedIcon)
        savedIcon.setOnClickListener {
            val intent = Intent(this, SavedActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        // Profile icon (stay on the same page)
        val profileIcon: ImageView = findViewById(R.id.profileIcon)
        profileIcon.setOnClickListener {
            // Stay on this page (Already on ProfileActivity)
        }

        // Predict icon
        val predictIcon: ImageView = findViewById(R.id.predictIcon)
        predictIcon.setOnClickListener {
            val intent = Intent(this, PredictActivity::class.java)
            startActivity(intent)
        }

        // Manage Listings icon
        val postIcon: ImageView = findViewById(R.id.postIcon)
        postIcon.setOnClickListener {
            val intent = Intent(this, ManageListingsActivity::class.java)
            startActivity(intent)
        }
    }
}
