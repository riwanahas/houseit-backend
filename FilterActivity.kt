package com.example.a279project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity

class FilterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        val areaField = findViewById<EditText>(R.id.areaField)
        val bedroomsSlider = findViewById<SeekBar>(R.id.bedroomsSlider)
        val bathroomsSlider = findViewById<SeekBar>(R.id.bathroomsSlider)
        val priceField = findViewById<EditText>(R.id.priceField)
        val applyFilterButton = findViewById<Button>(R.id.applyFilterButton)
        val clearFilterButton = findViewById<Button>(R.id.clearFilterButton)

        applyFilterButton.setOnClickListener {
            val filters = Intent().apply {
                putExtra("area", areaField.text.toString())
                putExtra("bedrooms", bedroomsSlider.progress)
                putExtra("bathrooms", bathroomsSlider.progress)
                putExtra("price", priceField.text.toString())
            }
            setResult(Activity.RESULT_OK, filters)
            finish()
        }

        clearFilterButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
