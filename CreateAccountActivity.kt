package com.example.a279project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var registerButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onStart() {
        super.onStart()

        // Check if the user is already signed in
        if (::auth.isInitialized) { // Ensure auth is initialized before accessing it
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Find views by their IDs
        val editTextName: TextInputEditText = findViewById(R.id.profileName)
        val editTextEmail: TextInputEditText = findViewById(R.id.inputEmail)
        val editTextPassword: TextInputEditText = findViewById(R.id.inputPassword)
        val editTextConfirmPassword: TextInputEditText = findViewById(R.id.inputConfirmPassword)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val textView: TextView = findViewById(R.id.loginNow)
        registerButton = findViewById(R.id.registerButton)

        // Navigate back to LoginActivity when "Login Now" is clicked
        textView.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Handle registration logic
        registerButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val name = editTextName.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()

            // Input validation
            if (name.isEmpty()) {
                Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }
            if (confirmPassword.isEmpty()) {
                Toast.makeText(this, "Confirm your Password", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            // Firebase Authentication: Create user
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        // Account creation success
                        val userId = task.result?.user?.uid
                        if (userId != null) {
                            // Save user details to SQLite database
                            val dbHelper = DatabaseHelper(this)
                            val rowId = dbHelper.insertUser(userId, name)
                            if (rowId != -1L) {
                                Toast.makeText(this, "Account created successfully.", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, SearchActivity::class.java)
                                startActivity(intent)
                                finish() // Close CreateAccountActivity
                            } else {
                                Toast.makeText(this, "Error saving user details to database.", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(this, "Error retrieving user ID.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // Account creation failure
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // Navigate back to LoginActivity when Back button is clicked
        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
