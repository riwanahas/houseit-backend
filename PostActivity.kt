package com.example.a279project

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class PostActivity : AppCompatActivity() {

    private val drawableImages = arrayOf(
        R.drawable.hamra, // Replace with your actual drawable resources
        R.drawable.koraytem,
        R.drawable.koraytem2
    )
    private var isLocalDrawableSelected = false
    private var selectedDrawableResId: Int = -1
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var firebaseAuth: FirebaseAuth
    private var currentUserId: String? = null
    private var isEditing: Boolean = false
    private var editingListingId: Int = -1
    private var originalImageUri: String? = null

    // Additional Views
    private lateinit var areaInput: EditText
    private lateinit var bedroomsInput: EditText
    private lateinit var bathroomsInput: EditText
    private lateinit var storiesInput: EditText
    private lateinit var mainroadGroup: RadioGroup
    private lateinit var guestroomGroup: RadioGroup
    private lateinit var basementGroup: RadioGroup
    private lateinit var hotWaterHeatingGroup: RadioGroup
    private lateinit var airConditioningGroup: RadioGroup
    private lateinit var parkingInput: EditText
    private lateinit var preferredAreaGroup: RadioGroup
    private lateinit var furnishingStatusSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        // Initialize Firebase Auth and get the current user ID
        firebaseAuth = FirebaseAuth.getInstance()
        currentUserId = firebaseAuth.currentUser?.uid

        // Initialize database helper
        dbHelper = DatabaseHelper(this)

        // Bind Views
        val backButton: ImageView = findViewById(R.id.backButton)
        val createButton: Button = findViewById(R.id.createButton)
        val titleInput: EditText = findViewById(R.id.titleInput)
        val priceInput: EditText = findViewById(R.id.priceInput)
        val addressInput: EditText = findViewById(R.id.addressInput)
        val descriptionInput: EditText = findViewById(R.id.descriptionInput)
        areaInput = findViewById(R.id.areaInput)
        bedroomsInput = findViewById(R.id.bedroomsInput)
        bathroomsInput = findViewById(R.id.bathroomsInput)
        storiesInput = findViewById(R.id.storiesInput)
        mainroadGroup = findViewById(R.id.mainroadGroup)
        guestroomGroup = findViewById(R.id.guestroomGroup)
        basementGroup = findViewById(R.id.basementGroup)
        hotWaterHeatingGroup = findViewById(R.id.hotWaterHeatingGroup)
        airConditioningGroup = findViewById(R.id.airConditioningGroup)
        parkingInput = findViewById(R.id.parkingInput)
        preferredAreaGroup = findViewById(R.id.preferredAreaGroup)
        furnishingStatusSpinner = findViewById(R.id.furnishingStatusSpinner)

        // Handle Back Button
        backButton.setOnClickListener { finish() }

        // Check if this is an edit operation
        if (intent.hasExtra("listingId")) {
            isEditing = true
            editingListingId = intent.getIntExtra("listingId", -1)

            titleInput.setText(intent.getStringExtra("title") ?: "")
            priceInput.setText(intent.getStringExtra("price") ?: "")
            addressInput.setText(intent.getStringExtra("address") ?: "")
            descriptionInput.setText(intent.getStringExtra("description") ?: "")
            areaInput.setText(intent.getStringExtra("area") ?: "")
            bedroomsInput.setText(intent.getStringExtra("bedrooms") ?: "")
            bathroomsInput.setText(intent.getStringExtra("bathrooms") ?: "")
            storiesInput.setText(intent.getStringExtra("stories") ?: "")

            val mainroad = intent.getStringExtra("mainroad") == "yes"
            val guestroom = intent.getStringExtra("guestroom") == "yes"
            val basement = intent.getStringExtra("basement") == "yes"
            val hotWaterHeating = intent.getStringExtra("hotWaterHeating") == "yes"
            val airConditioning = intent.getStringExtra("airConditioning") == "yes"
            val preferredArea = intent.getStringExtra("preferredArea") == "yes"


            mainroadGroup.check(if (mainroad) R.id.mainroadYes else R.id.mainroadNo)
            guestroomGroup.check(if (guestroom) R.id.guestroomYes else R.id.guestroomNo)
            basementGroup.check(if (basement) R.id.basementYes else R.id.basementNo)
            hotWaterHeatingGroup.check(if (hotWaterHeating) R.id.hotWaterYes else R.id.hotWaterNo)
            airConditioningGroup.check(if (airConditioning) R.id.airConditioningYes else R.id.airConditioningNo)

            parkingInput.setText(intent.getStringExtra("parking") ?: "")
            preferredAreaGroup.check(if (preferredArea) R.id.preferredAreaYes else R.id.preferredAreaNo)

            val furnishingStatus = intent.getStringExtra("furnishingStatus")
            furnishingStatusSpinner.setSelection(
                (furnishingStatusSpinner.adapter as ArrayAdapter<String>).getPosition(
                    furnishingStatus ?: ""
                )
            )

            originalImageUri = intent.getStringExtra("imageUri")
            if (!originalImageUri.isNullOrEmpty()) {
                val resId = resources.getIdentifier(
                    originalImageUri!!.replace("drawable/", ""),
                    "drawable",
                    packageName
                )
                val imageView = ImageView(this)
                imageView.setImageResource(resId)
                imageView.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                val imagePlaceholder: FrameLayout = findViewById(R.id.imagePlaceholder)
                imagePlaceholder.removeAllViews()
                imagePlaceholder.addView(imageView)
            }

            createButton.text = "UPDATE"
        }

        // Handle Image Selection
        val imagePlaceholder: FrameLayout = findViewById(R.id.imagePlaceholder)
        imagePlaceholder.setOnClickListener { showDrawablePickerDialog() }

        // Handle Create/Update Button
        createButton.setOnClickListener {
            // Retrieve Input Values
            val title = titleInput.text.toString().trim()
            val price = priceInput.text.toString().trim()
            val address = addressInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()
            val area = areaInput.text.toString().trim()
            val bedrooms = bedroomsInput.text.toString().trim()
            val bathrooms = bathroomsInput.text.toString().trim()
            val stories = storiesInput.text.toString().trim()
            val mainroad =
                if (mainroadGroup.checkedRadioButtonId == R.id.mainroadYes) "yes" else "no"
            val guestroom =
                if (guestroomGroup.checkedRadioButtonId == R.id.guestroomYes) "yes" else "no"
            val basement =
                if (basementGroup.checkedRadioButtonId == R.id.basementYes) "yes" else "no"
            val hotWaterHeating =
                if (hotWaterHeatingGroup.checkedRadioButtonId == R.id.hotWaterYes) "yes" else "no"
            val airConditioning =
                if (airConditioningGroup.checkedRadioButtonId == R.id.airConditioningYes) "yes" else "no"
            val parking = parkingInput.text.toString().trim()
            val preferredArea =
                if (preferredAreaGroup.checkedRadioButtonId == R.id.preferredAreaYes) "yes" else "no"
            val furnishingStatus = furnishingStatusSpinner.selectedItem.toString()

            // Input Validation
            if (title.isEmpty() || price.isEmpty() || address.isEmpty() || description.isEmpty() ||
                area.isEmpty() || bedrooms.isEmpty() || bathrooms.isEmpty() || stories.isEmpty() || parking.isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val imagePath = if (isLocalDrawableSelected) {
                "drawable/${resources.getResourceEntryName(selectedDrawableResId)}"
            } else {
                originalImageUri ?: run {
                    Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            if (isEditing) {
                // Update existing listing
                val rowsUpdated = dbHelper.updateListingWithDetails(
                    editingListingId,
                    title,
                    price,
                    address,
                    description,
                    imagePath,
                    area,
                    bedrooms,
                    bathrooms,
                    stories,
                    mainroad,
                    guestroom,
                    basement,
                    hotWaterHeating,
                    airConditioning,
                    parking,
                    preferredArea,
                    furnishingStatus
                )
                if (rowsUpdated > 0) {
                    Toast.makeText(this, "Listing updated successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to update listing", Toast.LENGTH_SHORT).show()
                }
            } else {


                // Save new listing
                currentUserId?.let { userId ->
                    val userName = dbHelper.getUserFullName(userId)
                    if (userName != null) {
                        val id = dbHelper.insertListing(
                            userId,
                            userName,
                            title,
                            price,
                            address,
                            description,
                            imagePath,
                            area,
                            bedrooms,
                            bathrooms,
                            stories,
                            mainroad,
                            guestroom,
                            basement,
                            hotWaterHeating,
                            airConditioning,
                            parking,
                            preferredArea,
                            furnishingStatus
                        )
                        if (id > 0) {
                            Toast.makeText(
                                this,
                                "Listing created successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to save listing", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(this, "Failed to retrieve user details", Toast.LENGTH_SHORT)
                            .show()
                    }
                } ?: run {
                    Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDrawablePickerDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select an Image")

        val gridView = GridView(this).apply {
            numColumns = 3
            adapter = ImageAdapter(drawableImages)
        }

        builder.setView(gridView)
        val dialog = builder.create()
        dialog.show()

        gridView.setOnItemClickListener { _, _, position, _ ->
            isLocalDrawableSelected = true
            selectedDrawableResId = drawableImages[position]

            // Update UI to show selected drawable
            val imageView = ImageView(this)
            imageView.setImageResource(drawableImages[position])
            imageView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            val imagePlaceholder: FrameLayout = findViewById(R.id.imagePlaceholder)
            imagePlaceholder.removeAllViews()
            imagePlaceholder.addView(imageView)

            dialog.dismiss()
        }
    }

    private inner class ImageAdapter(private val images: Array<Int>) : BaseAdapter() {
        override fun getCount() = images.size
        override fun getItem(position: Int) = images[position]
        override fun getItemId(position: Int) = position.toLong()
        override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup?): android.view.View {
            val imageView = ImageView(this@PostActivity)
            imageView.setImageResource(images[position])
            imageView.layoutParams = AbsListView.LayoutParams(200, 200)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            return imageView
        }
    }
}