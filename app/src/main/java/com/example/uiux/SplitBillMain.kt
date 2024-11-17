package com.example.uiux

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class SplitBillMain : AppCompatActivity() {

    private lateinit var tripButtonContainer: LinearLayout
    private var tripCounter = 1 // Counter to create unique trip names

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split_bill_main)

        tripButtonContainer = findViewById(R.id.trip_button_container)
        val addTripButton: ImageButton = findViewById(R.id.add_trip_button)

        // Add a new trip button dynamically
        addTripButton.setOnClickListener {
            showTripNameDialog()
        }
    }

    private fun showTripNameDialog() {
        // Inflate the dialog view
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_trip_name, null)
        val tripNameEditText = dialogView.findViewById<EditText>(R.id.trip_name_edittext)

        // Build the AlertDialog
        AlertDialog.Builder(this)
            .setTitle("Enter Trip Name")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val tripName = tripNameEditText.text.toString().trim()
                if (tripName.isNotEmpty()) {
                    addNewTrip(tripName) // Add the trip with the user-defined name
                } else {
                    Toast.makeText(this, "Trip name cannot be empty", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Adds a new trip button to the layout
    private fun addNewTrip(tripName: String) {
        val newButton = Button(this)
        newButton.text = tripName
        newButton.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // OnClick: Open TripDetailsActivity for the selected trip
        newButton.setOnClickListener {
            val intent = Intent(this, TripDetailsActivity::class.java)
            intent.putExtra("trip_name", tripName)
            startActivity(intent)
        }

        tripButtonContainer.addView(newButton)
    }

    fun BackButton(view: View?) {
        val intent = Intent(
            this@SplitBillMain,
            SecondTimeUserHome::class.java
        )
        startActivity(intent)
    }
}