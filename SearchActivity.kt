package com.example.a279project

import PropertyAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {
    private lateinit var propertyAdapter: PropertyAdapter
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_screen) // Ensure this layout is correct

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Fetch all listings with user names from the database
        val propertyList = dbHelper.getAllListingsWithUserNames().map {
            Property(
                profilePicture = R.drawable.ic_profile_image, // Replace with default profile image
                propertyImage = resources.getIdentifier(
                    it[DatabaseHelper.COLUMN_IMAGE_URI]?.replace("drawable/", ""),
                    "drawable",
                    packageName
                ),
                profileName = it[DatabaseHelper.COLUMN_USER_FULL_NAME] ?: "Unknown User",
                price = it[DatabaseHelper.COLUMN_PRICE] ?: "",
                address = it[DatabaseHelper.COLUMN_ADDRESS] ?: "",
                description = it[DatabaseHelper.COLUMN_DESCRIPTION] ?: "",
                area = it[DatabaseHelper.COLUMN_AREA] ?: "",
                bedrooms = it[DatabaseHelper.COLUMN_BEDROOMS] ?: "",
                bathrooms = it[DatabaseHelper.COLUMN_BATHROOMS] ?: "",
                stories = it[DatabaseHelper.COLUMN_STORIES] ?: "",
                mainroad = it[DatabaseHelper.COLUMN_MAINROAD] ?: "",
                guestroom = it[DatabaseHelper.COLUMN_GUESTROOM] ?: "",
                basement = it[DatabaseHelper.COLUMN_BASEMENT] ?: "No",
                hotWaterHeating = it[DatabaseHelper.COLUMN_HOT_WATER_HEATING] ?: "No",
                airConditioning = it[DatabaseHelper.COLUMN_AIR_CONDITIONING] ?: "No",
                parking = it[DatabaseHelper.COLUMN_PARKING] ?: "0",
                preferredArea = it[DatabaseHelper.COLUMN_PREF_AREA] ?: "Unknown",
                furnishingStatus = it[DatabaseHelper.COLUMN_FURNISHING_STATUS] ?: "",
                title = it[DatabaseHelper.COLUMN_TITLE] ?: "",
                id = it[DatabaseHelper.COLUMN_ID]?.toInt() ?: 0,
            )
        }

        // Set up RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.propertyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize PropertyAdapter and set it to RecyclerView
        propertyAdapter = PropertyAdapter(propertyList.toMutableList(), this)
        recyclerView.adapter = propertyAdapter

        // Set up search bar
        val searchBar = findViewById<EditText>(R.id.searchField)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                val filteredList = propertyList.filter {
                    it.address.lowercase().contains(query) ||
                            it.price.lowercase().contains(query) ||
                            it.profileName.lowercase().contains(query)
                }
                propertyAdapter.updateList(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {}
        })





        // Bottom Navigation Icons Click Listeners
        val profileIcon = findViewById<ImageView>(R.id.profileIcon)
        profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
        }

        val savedIcon = findViewById<ImageView>(R.id.savedIcon)
        savedIcon.setOnClickListener {
            val intent = Intent(this, SavedActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
        }

        val searchIcon = findViewById<ImageView>(R.id.searchIcon)
        searchIcon.setOnClickListener {
            // Already on SearchActivity, no action needed
        }

        val predictIcon: ImageView = findViewById(R.id.predictIcon)
        predictIcon.setOnClickListener {
            val intent = Intent(this, PredictActivity::class.java)
            startActivity(intent)
        }

        val postIcon: ImageView = findViewById(R.id.postIcon)
        postIcon.setOnClickListener {
            val intent = Intent(this, ManageListingsActivity::class.java)
            startActivity(intent)
        }

        // Navigate to FilterActivity
        val filterIcon: ImageView = findViewById(R.id.filterIcon)
        filterIcon.setOnClickListener {
            val intent = Intent(this, FilterActivity::class.java)
            startActivityForResult(intent, 1001)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK && data != null) {
            val area = data.getStringExtra("area")?.toIntOrNull()
            val bedrooms = data.getIntExtra("bedrooms", 0)
            val bathrooms = data.getIntExtra("bathrooms", 0)
            val price = data.getStringExtra("price")?.toIntOrNull()

            val filteredList = dbHelper.getAllListingsWithUserNames().filter { listing ->
                val listingArea = listing[DatabaseHelper.COLUMN_AREA]?.toIntOrNull() ?: 0
                val listingBedrooms = listing[DatabaseHelper.COLUMN_BEDROOMS]?.toIntOrNull() ?: 0
                val listingBathrooms = listing[DatabaseHelper.COLUMN_BATHROOMS]?.toIntOrNull() ?: 0
                val listingPrice = listing[DatabaseHelper.COLUMN_PRICE]?.toIntOrNull() ?: Int.MAX_VALUE

                (area == null || listingArea >= area) &&
                        (bedrooms == 0 || listingBedrooms >= bedrooms) &&
                        (bathrooms == 0 || listingBathrooms >= bathrooms) &&
                        (price == null || listingPrice <= price)
            }.map {
                Property(
                    profilePicture = R.drawable.ic_profile_image,
                    propertyImage = resources.getIdentifier(
                        it[DatabaseHelper.COLUMN_IMAGE_URI]?.replace("drawable/", ""),
                        "drawable",
                        packageName
                    ),
                    profileName = it[DatabaseHelper.COLUMN_USER_FULL_NAME] ?: "Unknown User",
                    price = it[DatabaseHelper.COLUMN_PRICE] ?: "",
                    address = it[DatabaseHelper.COLUMN_ADDRESS] ?: "",
                    description = it[DatabaseHelper.COLUMN_DESCRIPTION] ?: "",
                    area = it[DatabaseHelper.COLUMN_AREA] ?: "",
                    bedrooms = it[DatabaseHelper.COLUMN_BEDROOMS] ?: "",
                    bathrooms = it[DatabaseHelper.COLUMN_BATHROOMS] ?: "",
                    stories = it[DatabaseHelper.COLUMN_STORIES] ?: "",
                    mainroad = it[DatabaseHelper.COLUMN_MAINROAD] ?: "",
                    guestroom = it[DatabaseHelper.COLUMN_GUESTROOM] ?: "",
                    basement = it[DatabaseHelper.COLUMN_BASEMENT] ?: "No",
                    hotWaterHeating = it[DatabaseHelper.COLUMN_HOT_WATER_HEATING] ?: "No",
                    airConditioning = it[DatabaseHelper.COLUMN_AIR_CONDITIONING] ?: "No",
                    parking = it[DatabaseHelper.COLUMN_PARKING] ?: "0",
                    preferredArea = it[DatabaseHelper.COLUMN_PREF_AREA] ?: "Unknown",
                    furnishingStatus = it[DatabaseHelper.COLUMN_FURNISHING_STATUS] ?: "",
                    title = it[DatabaseHelper.COLUMN_TITLE] ?: "",
                    id = it[DatabaseHelper.COLUMN_ID]?.toInt() ?: 0
                )
            }
            propertyAdapter.updateList(filteredList)
        } else if (requestCode == 1001 && resultCode == Activity.RESULT_CANCELED) {
            // Show all listings when filters are cleared
            val allListings = dbHelper.getAllListingsWithUserNames().map {
                Property(
                    profilePicture = R.drawable.ic_profile_image,
                    propertyImage = resources.getIdentifier(
                        it[DatabaseHelper.COLUMN_IMAGE_URI]?.replace("drawable/", ""),
                        "drawable",
                        packageName
                    ),
                    profileName = it[DatabaseHelper.COLUMN_USER_FULL_NAME] ?: "Unknown User",
                    price = it[DatabaseHelper.COLUMN_PRICE] ?: "",
                    address = it[DatabaseHelper.COLUMN_ADDRESS] ?: "",
                    description = it[DatabaseHelper.COLUMN_DESCRIPTION] ?: "",
                    area = it[DatabaseHelper.COLUMN_AREA] ?: "",
                    bedrooms = it[DatabaseHelper.COLUMN_BEDROOMS] ?: "",
                    bathrooms = it[DatabaseHelper.COLUMN_BATHROOMS] ?: "",
                    stories = it[DatabaseHelper.COLUMN_STORIES] ?: "",
                    mainroad = it[DatabaseHelper.COLUMN_MAINROAD] ?: "",
                    guestroom = it[DatabaseHelper.COLUMN_GUESTROOM] ?: "",
                    basement = it[DatabaseHelper.COLUMN_BASEMENT] ?: "No",
                    hotWaterHeating = it[DatabaseHelper.COLUMN_HOT_WATER_HEATING] ?: "No",
                    airConditioning = it[DatabaseHelper.COLUMN_AIR_CONDITIONING] ?: "No",
                    parking = it[DatabaseHelper.COLUMN_PARKING] ?: "0",
                    preferredArea = it[DatabaseHelper.COLUMN_PREF_AREA] ?: "Unknown",
                    furnishingStatus = it[DatabaseHelper.COLUMN_FURNISHING_STATUS] ?: "",
                    title = it[DatabaseHelper.COLUMN_TITLE] ?: "",
                    id = it[DatabaseHelper.COLUMN_ID]?.toInt() ?: 0
                )
            }
            propertyAdapter.updateList(allListings)
        }
    }
}
