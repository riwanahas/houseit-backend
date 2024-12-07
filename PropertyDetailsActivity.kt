package com.example.a279project

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PropertyDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_details)

        // Find views
        val propertyImage = findViewById<ImageView>(R.id.propertyImage)
        val propertyPrice = findViewById<TextView>(R.id.propertyPrice)
        val propertyAddress = findViewById<TextView>(R.id.propertyAddress)
        val propertyDescription = findViewById<TextView>(R.id.propertyDescription)
        val propertyArea = findViewById<TextView>(R.id.propertyArea)
        val propertyBedrooms = findViewById<TextView>(R.id.propertyBedrooms)
        val propertyBathrooms = findViewById<TextView>(R.id.propertyBathrooms)
        val propertyStories = findViewById<TextView>(R.id.propertyStories)
        val propertyMainRoad = findViewById<TextView>(R.id.propertyMainRoad)
        val propertyGuestRoom = findViewById<TextView>(R.id.propertyGuestRoom)
        val propertyFurnishingStatus = findViewById<TextView>(R.id.propertyFurnishing)
        val propertyOwner = findViewById<TextView>(R.id.propertyOwner)
        val propertyTitle = findViewById<TextView>(R.id.propertyTitle)

        // Back Button
        val backButton = findViewById<ImageView>(R.id.backButton)

        // Retrieve data from intent
        val imageResId = intent.getIntExtra("PROPERTY_IMAGE", -1)
        val profilePicId = intent.getIntExtra("PROFILE_PICTURE", -1)
        val name = intent.getStringExtra("PROFILE_NAME") ?: "Unknown"
        val price = intent.getStringExtra("PRICE") ?: "Unknown"
        val address = intent.getStringExtra("ADDRESS") ?: "Unknown"
        val description = intent.getStringExtra("DESCRIPTION") ?: "No description available"
        val area = intent.getStringExtra("AREA") ?: "Unknown"
        val bedrooms = intent.getStringExtra("BEDROOMS") ?: "Unknown"
        val bathrooms = intent.getStringExtra("BATHROOMS") ?: "Unknown"
        val stories = intent.getStringExtra("STORIES") ?: "Unknown"
        val mainroad = intent.getStringExtra("MAINROAD") ?: "Unknown"
        val guestroom = intent.getStringExtra("GUESTROOM") ?: "Unknown"
        val furnishingStatus = intent.getStringExtra("FURNISHING_STATUS") ?: "Unknown"
        val title = intent.getStringExtra("TITLE")?: "No Title"

        // Set data to views
        if (imageResId != -1) propertyImage.setImageResource(imageResId)

        propertyPrice.text = "Price: $price $"
        propertyAddress.text = "Address: $address"
        propertyDescription.text = "Additional Information: $description"
        propertyArea.text = "Area: $area sq ft"
        propertyBedrooms.text = "Bedrooms: $bedrooms"
        propertyBathrooms.text = "Bathrooms: $bathrooms"
        propertyStories.text = "Stories: $stories"
        propertyMainRoad.text = "Main Road Access: $mainroad"
        propertyGuestRoom.text = "Guest Room: $guestroom"
        propertyFurnishingStatus.text = "Furnishing: $furnishingStatus"
        propertyOwner.text = "Owner: $name"
        propertyTitle.text = "$title"

        backButton.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Clears all activities above SearchActivity
            startActivity(intent)
        }
    }

}