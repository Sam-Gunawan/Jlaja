package com.example.jlaja

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File


class SplitBillMain : AppCompatActivity() {

    private lateinit var tripButtonContainer: LinearLayout
    private val tripList = mutableListOf<String>() // List to store trip names
    private val fileName = "trips.json" // File to save trip data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split_bill_main)

        tripButtonContainer = findViewById(R.id.trip_button_container)
        val addTripButton: Button = findViewById(R.id.add_trip_button)

        // Load saved trips and recreate buttons
        loadTripsFromFile()?.let { savedTrips ->
            tripList.addAll(savedTrips)
            savedTrips.forEach { tripName ->
                addNewTripButton(tripName)
            }
        }

        // Add a new trip button dynamically
        addTripButton.setOnClickListener {
            showTripNameDialog()
        }
    }

    private fun showTripNameDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_trip_name, null)
        val tripNameEditText = dialogView.findViewById<EditText>(R.id.trip_name_edittext)

        AlertDialog.Builder(this)
            .setTitle("Enter Trip Name")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val tripName = tripNameEditText.text.toString().trim()
                if (tripName.isNotEmpty()) {
                    tripList.add(tripName) // Add to the list
                    saveTripsToFile() // Save updated list to file
                    addNewTripButton(tripName) // Add a button for the new trip
                } else {
                    Toast.makeText(this, "Trip name cannot be empty", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Adds a new trip button to the layout
    private fun addNewTripButton(tripName: String) {
        val newButton = Button(this)
        newButton.text = tripName
        newButton.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        newButton.setOnClickListener {
            val intent = Intent(this, TripDetailsActivity::class.java)
            intent.putExtra("trip_name", tripName)
            startActivity(intent)
        }

        tripButtonContainer.addView(newButton)
    }

    // Saves the trip list to a JSON file and logs the file path
    private fun saveTripsToFile() {
        val gson = Gson()
        val jsonString = gson.toJson(tripList)
        val file = File(filesDir, fileName)
        file.writeText(jsonString)

        // Log the file path
        Log.d("SplitBillMain", "Trips saved to file: ${file.absolutePath}")
    }

    // Loads the trip list from a JSON file
    private fun loadTripsFromFile(): List<String>? {
        val file = File(filesDir, fileName)
        if (!file.exists()) return null

        val jsonString = file.readText()
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(jsonString, type)
    }

    fun BackButton(view: View?) {
        onBackPressed()
    }
}