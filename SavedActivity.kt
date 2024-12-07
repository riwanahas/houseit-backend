package com.example.a279project

import PropertyAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class SavedActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var savedAdapter: PropertyAdapter
    private lateinit var savedRecyclerView: RecyclerView
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saved_page)

        // Initialize DatabaseHelper and RecyclerView
        databaseHelper = DatabaseHelper(this)
        savedRecyclerView = findViewById(R.id.savedRecyclerView)
        savedRecyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch saved properties for the current user
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val savedProperties = fetchSavedProperties(userId).toMutableList()

            // Set up the adapter with saved properties
            savedAdapter = PropertyAdapter(savedProperties, this) { property ->
                // Remove property from the list dynamically
                savedProperties.remove(property)
                savedAdapter.notifyDataSetChanged()
            }
            savedRecyclerView.adapter = savedAdapter
        } else {
            // Handle case when the user is not logged in
            Toast.makeText(this, "Please log in to view saved properties.", Toast.LENGTH_SHORT).show()
        }

        // Set up bottom navigation
        setupBottomNavigation()
    }

    /**
     * Fetch saved properties from the database for a specific user.
     */
    private fun fetchSavedProperties(userId: String): List<Property> {
        // Use getSavedListingsForUser to fetch the saved listings for the current user
        val savedListings = databaseHelper.getSavedListingsForUser(userId)
        return savedListings.map { listing ->
            Property(
                profilePicture = R.drawable.ic_profile_image,
                propertyImage = resources.getIdentifier(
                    listing[DatabaseHelper.COLUMN_IMAGE_URI]?.replace("drawable/", ""),
                    "drawable",
                    packageName
                ),
                profileName = listing[DatabaseHelper.COLUMN_USER_FULL_NAME] ?: "Unknown User",
                price = listing[DatabaseHelper.COLUMN_PRICE] ?: "",
                address = listing[DatabaseHelper.COLUMN_ADDRESS] ?: "",
                description = listing[DatabaseHelper.COLUMN_DESCRIPTION] ?: "",
                area = listing[DatabaseHelper.COLUMN_AREA] ?: "",
                bedrooms = listing[DatabaseHelper.COLUMN_BEDROOMS] ?: "",
                bathrooms = listing[DatabaseHelper.COLUMN_BATHROOMS] ?: "",
                stories = listing[DatabaseHelper.COLUMN_STORIES] ?: "",
                mainroad = listing[DatabaseHelper.COLUMN_MAINROAD] ?: "",
                guestroom = listing[DatabaseHelper.COLUMN_GUESTROOM] ?: "",
                basement = listing[DatabaseHelper.COLUMN_BASEMENT] ?: "No",
                hotWaterHeating = listing[DatabaseHelper.COLUMN_HOT_WATER_HEATING] ?: "No",
                airConditioning = listing[DatabaseHelper.COLUMN_AIR_CONDITIONING] ?: "No",
                parking = listing[DatabaseHelper.COLUMN_PARKING] ?: "0",
                preferredArea = listing[DatabaseHelper.COLUMN_PREF_AREA] ?: "Unknown",
                furnishingStatus = listing[DatabaseHelper.COLUMN_FURNISHING_STATUS] ?: "",
                title = listing[DatabaseHelper.COLUMN_TITLE] ?: "",
                id = listing[DatabaseHelper.COLUMN_ID]?.toInt() ?: 0
            )
        }
    }

    /**
     * Set up bottom navigation icons and their actions.
     */
    private fun setupBottomNavigation() {
        val searchIcon: ImageView = findViewById(R.id.searchIcon)
        val profileIcon: ImageView = findViewById(R.id.profileIcon)
        val predictIcon: ImageView = findViewById(R.id.predictIcon)
        val postIcon: ImageView = findViewById(R.id.postIcon)

        searchIcon.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        profileIcon.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        predictIcon.setOnClickListener {
            startActivity(Intent(this, PredictActivity::class.java))
        }
        postIcon.setOnClickListener {
            startActivity(Intent(this, ManageListingsActivity::class.java))
        }
    }
}
