package com.example.a279project

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "listings.db"
        private const val DATABASE_VERSION = 1

        // Listings Table
        const val TABLE_LISTINGS = "listings"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_PRICE = "price"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_IMAGE_URI = "image_uri"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_USER_FULL_NAME = "user_full_name"
        const val COLUMN_AREA = "area"
        const val COLUMN_BEDROOMS = "bedrooms"
        const val COLUMN_BATHROOMS = "bathrooms"
        const val COLUMN_STORIES = "stories"
        const val COLUMN_MAINROAD = "mainroad"
        const val COLUMN_GUESTROOM = "guestroom"
        const val COLUMN_FURNISHING_STATUS = "furnishing_status"
        const val COLUMN_BASEMENT = "basement"
        const val COLUMN_HOT_WATER_HEATING = "hot_water_heating"
        const val COLUMN_AIR_CONDITIONING = "air_conditioning"
        const val COLUMN_PARKING = "parking"
        const val COLUMN_PREF_AREA = "preferred_area"

        // Users Table
        const val TABLE_USERS = "users"
        const val COLUMN_USER_NAME = "full_name"

        // Saved Table
        const val TABLE_SAVED = "saved"
        const val COLUMN_SAVED_ID = "saved_id"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create Listings Table
        val createListingsTableQuery = """
            CREATE TABLE $TABLE_LISTINGS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_PRICE TEXT NOT NULL,
                $COLUMN_ADDRESS TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_IMAGE_URI TEXT,
                $COLUMN_USER_ID TEXT NOT NULL,
                $COLUMN_USER_FULL_NAME TEXT NOT NULL,
                $COLUMN_AREA TEXT NOT NULL,
                $COLUMN_BEDROOMS TEXT NOT NULL,
                $COLUMN_BATHROOMS TEXT NOT NULL,
                $COLUMN_STORIES TEXT NOT NULL,
                $COLUMN_MAINROAD TEXT NOT NULL,
                $COLUMN_GUESTROOM TEXT NOT NULL,
                $COLUMN_FURNISHING_STATUS TEXT NOT NULL,
                $COLUMN_BASEMENT TEXT NOT NULL,
                $COLUMN_HOT_WATER_HEATING TEXT NOT NULL,
                $COLUMN_AIR_CONDITIONING TEXT NOT NULL,
                $COLUMN_PARKING INTEGER NOT NULL,
                $COLUMN_PREF_AREA TEXT NOT NULL
            )
        """

        // Create Users Table
        val createUsersTableQuery = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID TEXT PRIMARY KEY,
                $COLUMN_USER_NAME TEXT NOT NULL
            )
        """

        db?.execSQL(createListingsTableQuery)
        db?.execSQL(createUsersTableQuery)


        val createSavedTableQuery = """
    CREATE TABLE $TABLE_SAVED (
        $COLUMN_SAVED_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_USER_ID TEXT NOT NULL,
        $COLUMN_ID INTEGER NOT NULL,
        FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID),
        FOREIGN KEY($COLUMN_ID) REFERENCES $TABLE_LISTINGS($COLUMN_ID)
    )
"""
        db?.execSQL(createSavedTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop existing tables and recreate them
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_LISTINGS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun insertUser(userId: String, fullName: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_USER_NAME, fullName)
        }
        return db.insert(TABLE_USERS, null, values)
    }

    @SuppressLint("Range")
    fun getUserFullName(userId: String): String? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_NAME),
            "$COLUMN_USER_ID = ?",
            arrayOf(userId),
            null,
            null,
            null
        )

        var fullName: String? = null
        if (cursor.moveToFirst()) {
            fullName = cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME))
        }
        cursor.close()
        return fullName
    }

    fun insertListing(
        userId: String,
        userName: String,
        title: String,
        price: String,
        address: String,
        description: String,
        imageUri: String,
        area: String,
        bedrooms: String,
        bathrooms: String,
        stories: String,
        mainroad: String,
        guestroom: String,
        preferredArea: String,
        basement: String,
        hotWaterHeating: String,
        parking: String,
        airConditioning: String,
        furnishingStatus: String
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_USER_FULL_NAME, userName)
            put(COLUMN_TITLE, title)
            put(COLUMN_PRICE, price)
            put(COLUMN_ADDRESS, address)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_IMAGE_URI, imageUri)
            put(COLUMN_AREA, area)
            put(COLUMN_BEDROOMS, bedrooms)
            put(COLUMN_BATHROOMS, bathrooms)
            put(COLUMN_STORIES, stories)
            put(COLUMN_MAINROAD, mainroad)
            put(COLUMN_GUESTROOM, guestroom)
            put(COLUMN_FURNISHING_STATUS, furnishingStatus)
            put(COLUMN_BASEMENT, basement)
            put(COLUMN_HOT_WATER_HEATING, hotWaterHeating)
            put(COLUMN_AIR_CONDITIONING, airConditioning)
            put(COLUMN_PARKING, parking)
            put(COLUMN_PREF_AREA, preferredArea)
        }
        Log.d("DatabaseHelper", "Inserting: $values")
        return db.insert(TABLE_LISTINGS, null, values)
    }

    fun isListingAlreadySaved(title: String, userId: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_LISTINGS,
            arrayOf(COLUMN_ID),
            "$COLUMN_TITLE = ? AND $COLUMN_USER_ID = ?",
            arrayOf(title, userId),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    @SuppressLint("Range")
    fun getAllListingsWithUserNames(): List<Map<String, String>> {
        val db = readableDatabase
        val query = """
            SELECT 
                $COLUMN_ID, 
                $COLUMN_TITLE, 
                $COLUMN_PRICE, 
                $COLUMN_ADDRESS, 
                $COLUMN_DESCRIPTION, 
                $COLUMN_IMAGE_URI, 
                $COLUMN_AREA, 
                $COLUMN_BEDROOMS, 
                $COLUMN_BATHROOMS, 
                $COLUMN_STORIES, 
                $COLUMN_MAINROAD, 
                $COLUMN_GUESTROOM, 
                $COLUMN_FURNISHING_STATUS, 
                $COLUMN_BASEMENT,
                $COLUMN_HOT_WATER_HEATING,
                $COLUMN_AIR_CONDITIONING,
                $COLUMN_PARKING,
                $COLUMN_PREF_AREA,
                $COLUMN_USER_FULL_NAME
            FROM $TABLE_LISTINGS
        """
        val cursor = db.rawQuery(query, null)

        val listings = mutableListOf<Map<String, String>>()
        while (cursor.moveToNext()) {
            val listing = mapOf(
                COLUMN_ID to cursor.getInt(cursor.getColumnIndex(COLUMN_ID)).toString(),
                COLUMN_TITLE to cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                COLUMN_PRICE to cursor.getString(cursor.getColumnIndex(COLUMN_PRICE)),
                COLUMN_ADDRESS to cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)),
                COLUMN_DESCRIPTION to cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                COLUMN_IMAGE_URI to cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URI)),
                COLUMN_AREA to cursor.getString(cursor.getColumnIndex(COLUMN_AREA)),
                COLUMN_BEDROOMS to cursor.getString(cursor.getColumnIndex(COLUMN_BEDROOMS)),
                COLUMN_BATHROOMS to cursor.getString(cursor.getColumnIndex(COLUMN_BATHROOMS)),
                COLUMN_STORIES to cursor.getString(cursor.getColumnIndex(COLUMN_STORIES)),
                COLUMN_MAINROAD to cursor.getString(cursor.getColumnIndex(COLUMN_MAINROAD)),
                COLUMN_GUESTROOM to cursor.getString(cursor.getColumnIndex(COLUMN_GUESTROOM)),
                COLUMN_FURNISHING_STATUS to cursor.getString(
                    cursor.getColumnIndex(
                        COLUMN_FURNISHING_STATUS
                    )
                ),
                COLUMN_BASEMENT to cursor.getString(cursor.getColumnIndex(COLUMN_BASEMENT)),
                COLUMN_HOT_WATER_HEATING to cursor.getString(
                    cursor.getColumnIndex(
                        COLUMN_HOT_WATER_HEATING
                    )
                ),
                COLUMN_AIR_CONDITIONING to cursor.getString(
                    cursor.getColumnIndex(
                        COLUMN_AIR_CONDITIONING
                    )
                ),
                COLUMN_PARKING to cursor.getString(cursor.getColumnIndex(COLUMN_PARKING)),
                COLUMN_PREF_AREA to cursor.getString(cursor.getColumnIndex(COLUMN_PREF_AREA)),
                COLUMN_USER_FULL_NAME to cursor.getString(
                    cursor.getColumnIndex(
                        COLUMN_USER_FULL_NAME
                    )
                )
            )
            listings.add(listing)
        }
        cursor.close()
        return listings
    }

    @SuppressLint("Range")
    fun getListingsForUser(userId: String): List<Map<String, String>> {
        val db = readableDatabase
        val query = """
            SELECT 
                $COLUMN_ID, 
                $COLUMN_TITLE, 
                $COLUMN_PRICE, 
                $COLUMN_ADDRESS, 
                $COLUMN_DESCRIPTION, 
                $COLUMN_IMAGE_URI, 
                $COLUMN_USER_FULL_NAME,
                $COLUMN_AREA,
                $COLUMN_BEDROOMS,
                $COLUMN_BATHROOMS,
                $COLUMN_STORIES,
                $COLUMN_MAINROAD,
                $COLUMN_GUESTROOM,
                $COLUMN_FURNISHING_STATUS,
                $COLUMN_BASEMENT,
                $COLUMN_HOT_WATER_HEATING,
                $COLUMN_AIR_CONDITIONING,
                $COLUMN_PARKING,
                $COLUMN_PREF_AREA
            FROM $TABLE_LISTINGS
            WHERE $COLUMN_USER_ID = ?
        """
        val cursor = db.rawQuery(query, arrayOf(userId))

        val listings = mutableListOf<Map<String, String>>()
        while (cursor.moveToNext()) {
            val listing = mapOf(
                COLUMN_ID to cursor.getInt(cursor.getColumnIndex(COLUMN_ID)).toString(),
                COLUMN_TITLE to cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                COLUMN_PRICE to cursor.getString(cursor.getColumnIndex(COLUMN_PRICE)),
                COLUMN_ADDRESS to cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)),
                COLUMN_DESCRIPTION to cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                COLUMN_IMAGE_URI to cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URI)),
                COLUMN_USER_FULL_NAME to cursor.getString(
                    cursor.getColumnIndex(
                        COLUMN_USER_FULL_NAME
                    )
                ),
                COLUMN_AREA to cursor.getString(cursor.getColumnIndex(COLUMN_AREA)),
                COLUMN_BEDROOMS to cursor.getString(cursor.getColumnIndex(COLUMN_BEDROOMS)),
                COLUMN_BATHROOMS to cursor.getString(cursor.getColumnIndex(COLUMN_BATHROOMS)),
                COLUMN_STORIES to cursor.getString(cursor.getColumnIndex(COLUMN_STORIES)),
                COLUMN_MAINROAD to cursor.getString(cursor.getColumnIndex(COLUMN_MAINROAD)),
                COLUMN_GUESTROOM to cursor.getString(cursor.getColumnIndex(COLUMN_GUESTROOM)),
                COLUMN_FURNISHING_STATUS to cursor.getString(
                    cursor.getColumnIndex(
                        COLUMN_FURNISHING_STATUS
                    )
                ),
                COLUMN_BASEMENT to cursor.getString(cursor.getColumnIndex(COLUMN_BASEMENT)),
                COLUMN_HOT_WATER_HEATING to cursor.getString(
                    cursor.getColumnIndex(
                        COLUMN_HOT_WATER_HEATING
                    )
                ),
                COLUMN_AIR_CONDITIONING to cursor.getString(
                    cursor.getColumnIndex(
                        COLUMN_AIR_CONDITIONING
                    )
                ),
                COLUMN_PARKING to cursor.getString(cursor.getColumnIndex(COLUMN_PARKING)),
                COLUMN_PREF_AREA to cursor.getString(cursor.getColumnIndex(COLUMN_PREF_AREA))
            )
            listings.add(listing)
        }
        cursor.close()
        return listings
    }

    fun deleteListingById(listingId: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_LISTINGS, "$COLUMN_ID = ?", arrayOf(listingId.toString()))
    }

    fun updateListingWithDetails(
        listingId: Int,
        title: String,
        price: String,
        address: String,
        description: String,
        imageUri: String,
        area: String,
        bedrooms: String,
        bathrooms: String,
        stories: String,
        mainroad: String,
        guestroom: String,
        preferredArea: String,
        basement: String,
        hotWaterHeating: String,
        parking: String,
        airConditioning: String,
        furnishingStatus: String
    ): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_PRICE, price)
            put(COLUMN_ADDRESS, address)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_IMAGE_URI, imageUri)
            put(COLUMN_AREA, area)
            put(COLUMN_BEDROOMS, bedrooms)
            put(COLUMN_BATHROOMS, bathrooms)
            put(COLUMN_STORIES, stories)
            put(COLUMN_MAINROAD, mainroad)
            put(COLUMN_GUESTROOM, guestroom)
            put(COLUMN_FURNISHING_STATUS, furnishingStatus)
            put(COLUMN_BASEMENT, basement)
            put(COLUMN_HOT_WATER_HEATING, hotWaterHeating)
            put(COLUMN_AIR_CONDITIONING, airConditioning)
            put(COLUMN_PARKING, parking)
            put(COLUMN_PREF_AREA, preferredArea)
        }
        return db.update(TABLE_LISTINGS, values, "$COLUMN_ID = ?", arrayOf(listingId.toString()))
    }

    fun saveListing(userId: String, listingId: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_ID, listingId)
        }
        return db.insert(TABLE_SAVED, null, values)
    }

    fun removeSavedListing(userId: String, listingId: Int): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_SAVED,
            "$COLUMN_USER_ID = ? AND $COLUMN_ID = ?",
            arrayOf(userId, listingId.toString())
        )
    }

    fun isListingSaved(userId: String, listingId: Int): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_SAVED,
            arrayOf(COLUMN_SAVED_ID),
            "$COLUMN_USER_ID = ? AND $COLUMN_ID = ?",
            arrayOf(userId, listingId.toString()),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
    @SuppressLint("Range")
    fun getSavedListingsForUser(userId: String): List<Map<String, String>> {
        val db = readableDatabase
        val query = """
            SELECT l.*
            FROM $TABLE_SAVED s
            INNER JOIN $TABLE_LISTINGS l ON s.$COLUMN_ID = l.$COLUMN_ID
            WHERE s.$COLUMN_USER_ID = ?
        """
        val cursor = db.rawQuery(query, arrayOf(userId))

        val listings = mutableListOf<Map<String, String>>()
        while (cursor.moveToNext()) {
            val listing = mapOf(
                COLUMN_ID to cursor.getInt(cursor.getColumnIndex(COLUMN_ID)).toString(),
                COLUMN_TITLE to cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                COLUMN_PRICE to cursor.getString(cursor.getColumnIndex(COLUMN_PRICE)),
                COLUMN_ADDRESS to cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)),
                COLUMN_DESCRIPTION to cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                COLUMN_IMAGE_URI to cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URI)),
                COLUMN_USER_FULL_NAME to cursor.getString(cursor.getColumnIndex(COLUMN_USER_FULL_NAME)),
                COLUMN_AREA to cursor.getString(cursor.getColumnIndex(COLUMN_AREA)),
                COLUMN_BEDROOMS to cursor.getString(cursor.getColumnIndex(COLUMN_BEDROOMS)),
                COLUMN_BATHROOMS to cursor.getString(cursor.getColumnIndex(COLUMN_BATHROOMS)),
                COLUMN_STORIES to cursor.getString(cursor.getColumnIndex(COLUMN_STORIES)),
                COLUMN_MAINROAD to cursor.getString(cursor.getColumnIndex(COLUMN_MAINROAD)),
                COLUMN_GUESTROOM to cursor.getString(cursor.getColumnIndex(COLUMN_GUESTROOM)),
                COLUMN_FURNISHING_STATUS to cursor.getString(cursor.getColumnIndex(COLUMN_FURNISHING_STATUS)),
                COLUMN_BASEMENT to cursor.getString(cursor.getColumnIndex(COLUMN_BASEMENT)),
                COLUMN_HOT_WATER_HEATING to cursor.getString(cursor.getColumnIndex(COLUMN_HOT_WATER_HEATING)),
                COLUMN_AIR_CONDITIONING to cursor.getString(cursor.getColumnIndex(COLUMN_AIR_CONDITIONING)),
                COLUMN_PARKING to cursor.getString(cursor.getColumnIndex(COLUMN_PARKING)),
                COLUMN_PREF_AREA to cursor.getString(cursor.getColumnIndex(COLUMN_PREF_AREA))
            )
            listings.add(listing)
        }
        cursor.close()
        return listings
    }
}