import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.a279project.DatabaseHelper
import com.example.a279project.Property
import com.example.a279project.PropertyDetailsActivity
import com.example.a279project.R
import com.google.firebase.auth.FirebaseAuth

class PropertyAdapter(
    private val propertyList: MutableList<Property>, // Original property list
    private val context: Context,
    private val onUnlike: ((Property) -> Unit)? = null // Optional callback for unliking
) : RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>() {

    private val dbHelper = DatabaseHelper(context)
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var filteredList: MutableList<Property> = propertyList.toMutableList() // Filtered property list

    inner class PropertyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profilePicture: ImageView = view.findViewById(R.id.profilePicture) // User's profile picture
        val propertyImage: ImageView = view.findViewById(R.id.propertyImage)  // Property image
        val profileName: TextView = view.findViewById(R.id.profileName)        // User's full name
        val price: TextView = view.findViewById(R.id.price)                    // Property price
        val location: TextView = view.findViewById(R.id.location)              // Property address/location
        val heartIcon: ImageView = view.findViewById(R.id.heartIcon)           // Heart icon for save/remove
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.property_item, parent, false)
        return PropertyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = filteredList[position]
        val userId = firebaseAuth.currentUser?.uid

        // Bind property data to views
        holder.profilePicture.setImageResource(property.profilePicture) // User profile image
        holder.propertyImage.setImageResource(property.propertyImage)   // Property image
        holder.profileName.text = property.profileName                  // User's full name
        holder.price.text = "$${property.price}"                        // Property price
        holder.location.text = property.address                         // Property address

        // Set initial heart icon state
        if (userId != null) {
            val isSaved = dbHelper.isListingSaved(userId, property.id)
            holder.heartIcon.setImageResource(
                if (isSaved) R.drawable.ic_saved else R.drawable.ic_heart_empty
            )

            // Handle heart icon click
            holder.heartIcon.setOnClickListener {
                if (dbHelper.isListingSaved(userId, property.id)) {
                    dbHelper.removeSavedListing(userId, property.id)
                    holder.heartIcon.setImageResource(R.drawable.ic_heart_empty)
                    onUnlike?.invoke(property) // Notify SavedActivity
                } else {
                    dbHelper.saveListing(userId, property.id)
                    holder.heartIcon.setImageResource(R.drawable.ic_saved)
                }
            }
        } else {
            holder.heartIcon.setImageResource(R.drawable.ic_heart_empty)
            holder.heartIcon.setOnClickListener {
                Toast.makeText(context, "Please log in to save listings.", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener to navigate to PropertyDetailsActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PropertyDetailsActivity::class.java).apply {
                putExtra("PROFILE_PICTURE", property.profilePicture)
                putExtra("PROPERTY_IMAGE", property.propertyImage)
                putExtra("PROFILE_NAME", property.profileName)
                putExtra("PRICE", property.price)
                putExtra("ADDRESS", property.address)
                putExtra("DESCRIPTION", property.description)
                putExtra("AREA", property.area)
                putExtra("BEDROOMS", property.bedrooms)
                putExtra("BATHROOMS", property.bathrooms)
                putExtra("STORIES", property.stories)
                putExtra("MAINROAD", property.mainroad)
                putExtra("GUESTROOM", property.guestroom)
                putExtra("FURNISHING_STATUS", property.furnishingStatus)
                putExtra("TITLE", property.title)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = filteredList.size

    /**
     * Updates the property list with the filtered data and refreshes the RecyclerView.
     */
    fun updateList(newList: List<Property>) {
        filteredList.clear()
        filteredList.addAll(newList)
        notifyDataSetChanged()
    }

    /**
     * Resets the filtered list to show all properties.
     */
    fun resetFilter() {
        filteredList.clear()
        filteredList.addAll(propertyList)
        notifyDataSetChanged()
    }
}
