package com.example.a279project

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PredictResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_predict_result)

        // Set up the back button to go back to PredictActivity
        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish() // This will close PredictResultActivity and return to the previous activity
        }

        // Receive the predicted price from intent (if applicable)
        val predictedPriceValue = intent.getStringExtra("PREDICTED_PRICE") ?: "200 000$"

        // Set the predicted price in the TextView
        val predictedPriceTextView: TextView = findViewById(R.id.predictedPriceValue)
        predictedPriceTextView.text = predictedPriceValue
    }
}
