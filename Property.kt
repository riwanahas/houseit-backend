package com.example.a279project

data class Property(
    val profilePicture: Int,
    val propertyImage: Int,
    val profileName: String,
    val price: String,
    val address: String,
    val description: String,
    val area: String,
    val bedrooms: String,
    val bathrooms: String,
    val stories: String,
    val mainroad: String,
    val guestroom: String,
    val basement: String,             // New: Basement
    val hotWaterHeating: String,      // New: Hot Water Heating
    val airConditioning: String,      // New: Air Conditioning
    val parking: String,              // New: Parking
    val preferredArea: String,        // New: Preferred Area
    val furnishingStatus: String,
    val title: String,
    val id: Int,
)