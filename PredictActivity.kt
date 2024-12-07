package com.example.a279project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat
import java.util.*

class PredictActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_predict)

        // Navigation bar icons
        val searchIcon: ImageView = findViewById(R.id.searchIcon)
        val savedIcon: ImageView = findViewById(R.id.savedIcon)
        val profileIcon: ImageView = findViewById(R.id.profileIcon)

        searchIcon.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
        savedIcon.setOnClickListener {
            val intent = Intent(this, SavedActivity::class.java)
            startActivity(intent)
        }
        profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        val postIcon: ImageView = findViewById(R.id.postIcon)
        postIcon.setOnClickListener {
            val intent = Intent(this, ManageListingsActivity::class.java)
            startActivity(intent)
        }

        // Update text values as sliders are adjusted
        val bedroomsSlider: SeekBar = findViewById(R.id.bedroomsSlider)
        val bedroomsCount: TextView = findViewById(R.id.bedroomsCount)
        bedroomsSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                bedroomsCount.text = "$progress Bedrooms"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val bathroomsSlider: SeekBar = findViewById(R.id.bathroomsSlider)
        val bathroomsCount: TextView = findViewById(R.id.bathroomsCount)
        bathroomsSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                bathroomsCount.text = "$progress Bathrooms"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val storiesSlider: SeekBar = findViewById(R.id.storiesSlider)
        val storiesCount: TextView = findViewById(R.id.storiesCount)
        storiesSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                storiesCount.text = "$progress Stories"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val parkingSlider: SeekBar = findViewById(R.id.parkingSlider)
        val parkingCount: TextView = findViewById(R.id.parkingCount)
        parkingSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                parkingCount.text = "$progress Parking Spaces"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Predict button - fetch inputs and make API call
        val predictButton: Button = findViewById(R.id.predictButton)
        predictButton.setOnClickListener {
            try {
                // Collect values from the form
                val area = findViewById<EditText>(R.id.areaField).text.toString().toInt()
                val bedrooms = bedroomsSlider.progress
                val bathrooms = bathroomsSlider.progress
                val stories = storiesSlider.progress
                val mainroad = if (findViewById<Switch>(R.id.mainroadSwitch).isChecked) 1 else 0
                val guestroom = if (findViewById<Switch>(R.id.guestroomSwitch).isChecked) 1 else 0
                val basement = if (findViewById<Switch>(R.id.basementSwitch).isChecked) 1 else 0
                val hotwaterheating = if (findViewById<Switch>(R.id.hotwaterSwitch).isChecked) 1 else 0
                val airconditioning = if (findViewById<Switch>(R.id.airConditioningSwitch).isChecked) 1 else 0
                val parking = parkingSlider.progress
                val preferredArea = if (findViewById<Switch>(R.id.preferredAreaSwitch).isChecked) 1 else 0

                val furnishingGroup = findViewById<RadioGroup>(R.id.furnishingGroup)
                val furnishingStatus = when (furnishingGroup.checkedRadioButtonId) {
                    R.id.furnishedOption -> 0
                    R.id.semiFurnishedOption -> 1
                    R.id.unfurnishedOption -> 2
                    else -> 0
                }

                // Create JSON object for the API request
                val jsonObject = JSONObject()
                jsonObject.put("area", area)
                jsonObject.put("bedrooms", bedrooms)
                jsonObject.put("bathrooms", bathrooms)
                jsonObject.put("stories", stories)
                jsonObject.put("mainroad", mainroad)
                jsonObject.put("guestroom", guestroom)
                jsonObject.put("basement", basement)
                jsonObject.put("hotwaterheating", hotwaterheating)
                jsonObject.put("airconditioning", airconditioning)
                jsonObject.put("parking", parking)
                jsonObject.put("prefarea", preferredArea)
                jsonObject.put("furnishingstatus", furnishingStatus)

                // Send the data to the Flask API
                Thread {
                    try {
                        val url = URL("http://10.0.2.2:5000/predict")
                        val conn = url.openConnection() as HttpURLConnection
                        conn.requestMethod = "POST"
                        conn.setRequestProperty("Content-Type", "application/json")
                        conn.doOutput = true

                        // Write JSON data
                        val os: OutputStream = conn.outputStream
                        os.write(jsonObject.toString().toByteArray())
                        os.flush()
                        os.close()

                        // Read the response
                        if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                            val response = conn.inputStream.bufferedReader().use { it.readText() }
                            val predictedPrice = JSONObject(response).getDouble("predicted_price")

                            val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(predictedPrice.toInt()) + " $"

                            runOnUiThread {
                                // Navigate to PredictResultActivity with the prediction result
                                val intent = Intent(this@PredictActivity, PredictResultActivity::class.java)
                                intent.putExtra("PREDICTED_PRICE", formattedPrice)
                                startActivity(intent)
                            }
                        } else {
                            Log.e("HTTP_ERROR", "Server returned code: ${conn.responseCode}")
                        }
                        conn.disconnect()
                    } catch (e: Exception) {
                        Log.e("API_ERROR", e.toString())
                    }
                }.start()

            } catch (e: Exception) {
                Log.e("INPUT_ERROR", e.toString())
                Toast.makeText(this, "Invalid input. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
