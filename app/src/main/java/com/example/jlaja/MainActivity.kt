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
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jlaja.ui.theme.JlajaTheme

import com.example.jlaja.TripDetailsActivity
import com.example.jlaja.BillsAdapter
import com.example.jlaja.AddBillActivity
import com.example.jlaja.MembersAdapter


class MainActivity : AppCompatActivity() {

    private lateinit var tripButtonContainer: LinearLayout
    private var tripCounter = 1 // Counter to create unique trip names

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tripButtonContainer = findViewById(R.id.trip_button_container)
        val addTripButton: Button = findViewById(R.id.add_trip_button)

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
        android.app.AlertDialog.Builder(this)
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
}