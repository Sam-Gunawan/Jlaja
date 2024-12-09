package com.example.jlaja

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SplitBillMain : AppCompatActivity() {

    private lateinit var tripButtonContainer: LinearLayout
//    private var tripCounter = 1 // Counter to create unique trip names
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split_bill_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        tripButtonContainer = findViewById(R.id.trip_button_container)
        val addTripButton: Button = findViewById(R.id.add_trip_button)

//        // Add a new trip button dynamically
//        addTripButton.setOnClickListener {
//            showTripNameDialog()
//        }

        addTripButton.setOnClickListener {
            CreateTripDialog(this) { tripName, emails ->
                createTrip(tripName, emails)
            }.showDialog()
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

    private fun createTrip(tripName: String, usernames: List<String>) {
        val currentUserEmail = auth.currentUser?.email ?: return
        val tripData = hashMapOf(
            "name" to tripName,
            "members" to usernames,
            "createdBy" to currentUserEmail,
            "timestamp" to Timestamp.now()
        )

        db.collection("trips").add(tripData)
            .addOnSuccessListener {
                sendNotifications(usernames, tripName)
                Toast.makeText(this, "Trip created successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to create trip: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendNotifications(usernames: List<String>, tripName: String) {
        db.collection("users").whereIn("username", usernames).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val token = document.getString("fcmToken")
                    token?.let { sendFCMNotification(it, "New Trip Invitation", "You have been invited to $tripName!") }
                }
            }
    }

    private fun sendFCMNotification(token: String, title: String, message: String) {
        // Use Firebase Cloud Messaging SDK to send the notification
    }
}