package com.example.a279project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class ManageListingsActivity : AppCompatActivity() {

    private lateinit var listingsRecyclerView: RecyclerView
    private lateinit var listingsAdapter: ListingsAdapter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var listingsSubtitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_listings)

        // Initialize Firebase Authentication and DatabaseHelper
        firebaseAuth = FirebaseAuth.getInstance()
        dbHelper = DatabaseHelper(this)

        // Get reference to the listingsSubtitle TextView
        listingsSubtitle = findViewById(R.id.listingsSubtitle)

        // Get the current user ID
        val currentUserId = firebaseAuth.currentUser?.uid

        // If the user is not logged in, show a message and navigate back
        if (currentUserId == null) {
            Toast.makeText(this, "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Fetch user-specific listings from the database
        loadUserListings(currentUserId)

        // Button to navigate to PostActivity
        val addListingButton: Button = findViewById(R.id.addListingButton)
        addListingButton.setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            startActivity(intent)
        }

        // Navigation bar icons
        findViewById<ImageView>(R.id.searchIcon).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        findViewById<ImageView>(R.id.savedIcon).setOnClickListener {
            startActivity(Intent(this, SavedActivity::class.java))
        }
        findViewById<ImageView>(R.id.postIcon).setOnClickListener {
            startActivity(Intent(this, PostActivity::class.java))
        }
        findViewById<ImageView>(R.id.predictIcon).setOnClickListener {
            startActivity(Intent(this, PredictActivity::class.java))
        }
        findViewById<ImageView>(R.id.profileIcon).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val currentUserId = firebaseAuth.currentUser?.uid
        if (currentUserId != null) {
            loadUserListings(currentUserId)
        }
    }

    private fun loadUserListings(userId: String) {
        val listings = dbHelper.getListingsForUser(userId).map {
            Listing(
                id = it[DatabaseHelper.COLUMN_ID]?.toInt() ?: 0,
                title = it[DatabaseHelper.COLUMN_TITLE] ?: "",
                price = it[DatabaseHelper.COLUMN_PRICE] ?: "",
                address = it[DatabaseHelper.COLUMN_ADDRESS] ?: "",
                description = it[DatabaseHelper.COLUMN_DESCRIPTION] ?: "",
                area = it[DatabaseHelper.COLUMN_AREA] ?: "",
                bedrooms = it[DatabaseHelper.COLUMN_BEDROOMS] ?: "",
                bathrooms = it[DatabaseHelper.COLUMN_BATHROOMS] ?: "",
                stories = it[DatabaseHelper.COLUMN_STORIES] ?: "",
                mainroad = it[DatabaseHelper.COLUMN_MAINROAD] ?: "",
                guestroom = it[DatabaseHelper.COLUMN_GUESTROOM] ?: "",
                basement = it[DatabaseHelper.COLUMN_BASEMENT] ?: "",
                hotWaterHeating = it[DatabaseHelper.COLUMN_HOT_WATER_HEATING] ?: "",
                airConditioning = it[DatabaseHelper.COLUMN_AIR_CONDITIONING] ?: "",
                parking = it[DatabaseHelper.COLUMN_PARKING] ?: "",
                preferredArea = it[DatabaseHelper.COLUMN_PREF_AREA] ?: "",
                furnishingStatus = it[DatabaseHelper.COLUMN_FURNISHING_STATUS] ?: "",
                imageUri = it[DatabaseHelper.COLUMN_IMAGE_URI] ?: "",
                imageResId = resources.getIdentifier(
                    it[DatabaseHelper.COLUMN_IMAGE_URI]?.replace("drawable/", ""),
                    "drawable",
                    packageName
                ),
                userName = it[DatabaseHelper.COLUMN_USER_FULL_NAME] ?: "Unknown User"
            )
        }.toMutableList()

        // Update the listingsSubtitle TextView with the count of listings
        val listingCount = listings.size
        listingsSubtitle.text = "You have ($listingCount) listings"

        // Initialize RecyclerView and Adapter
        listingsRecyclerView = findViewById(R.id.listingsRecyclerView)
        listingsRecyclerView.layoutManager = LinearLayoutManager(this)
        listingsAdapter = ListingsAdapter(listings, { listing ->
            // Handle delete functionality
            dbHelper.deleteListingById(listing.id)
            Toast.makeText(this, "${listing.title} deleted successfully!", Toast.LENGTH_SHORT).show()
            loadUserListings(userId) // Reload the listings to refresh count and UI
        }, { listing ->
            // Handle edit functionality: Navigate to PostActivity
            val intent = Intent(this, PostActivity::class.java).apply {
                putExtra("listingId", listing.id)
                putExtra("title", listing.title)
                putExtra("price", listing.price)
                putExtra("address", listing.address)
                putExtra("description", listing.description)
                putExtra("area", listing.area)
                putExtra("bedrooms", listing.bedrooms)
                putExtra("bathrooms", listing.bathrooms)
                putExtra("stories", listing.stories)
                putExtra("mainroad", listing.mainroad)
                putExtra("guestroom", listing.guestroom)
                putExtra("basement", listing.basement)
                putExtra("hotWaterHeating", listing.hotWaterHeating)
                putExtra("airConditioning", listing.airConditioning)
                putExtra("parking", listing.parking)
                putExtra("preferredArea", listing.preferredArea)
                putExtra("furnishingStatus", listing.furnishingStatus)
                putExtra("imageUri", listing.imageUri)
            }
            startActivity(intent)
        })
        listingsRecyclerView.adapter = listingsAdapter
    }
}

// Updated Listing data class
data class Listing(
    val id: Int,
    val title: String,
    val price: String,
    val address: String,
    val description: String,
    val area: String,
    val bedrooms: String,
    val bathrooms: String,
    val stories: String,
    val mainroad: String,
    val guestroom: String,
    val furnishingStatus: String,
    val imageUri: String,
    val imageResId: Int,
    val userName: String,
    val basement: String,             // New: Basement
    val hotWaterHeating: String,      // New: Hot Water Heating
    val airConditioning: String,      // New: Air Conditioning
    val parking: String,              // New: Parking
    val preferredArea: String,
)

// Updated ListingsAdapter class
class ListingsAdapter(
    private val listings: MutableList<Listing>,
    private val onDelete: (Listing) -> Unit,
    private val onEdit: (Listing) -> Unit
) : RecyclerView.Adapter<ListingsAdapter.ListingViewHolder>() {

    inner class ListingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val listingImage: ImageView = view.findViewById(R.id.listingImage)
        val listingTitle: TextView = view.findViewById(R.id.listingTitle)
        val editIcon: ImageView = view.findViewById(R.id.editIcon)
        val deleteIcon: ImageView = view.findViewById(R.id.deleteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listing_item, parent, false)
        return ListingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        val listing = listings[position]
        holder.listingTitle.text = listing.title
        holder.listingImage.setImageResource(listing.imageResId)

        // Handle delete functionality
        holder.deleteIcon.setOnClickListener {
            onDelete(listing)
            listings.removeAt(position)
            notifyItemRemoved(position)
        }

        // Handle edit functionality
        holder.editIcon.setOnClickListener {
            onEdit(listing)
        }
    }

    override fun getItemCount() = listings.size
}
