package com.example.uiux

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class TripDetailsActivity : AppCompatActivity() {

    private lateinit var billsRecyclerView: RecyclerView
    private lateinit var addBillButton: Button
    private lateinit var tripNameTextView: TextView
    private lateinit var totalBillsTextView: TextView
    private lateinit var tripDetailButtonContainer: LinearLayout // Initialize this container



    private val billsList = mutableListOf<Bill>()
    private var totalBillsCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_detail)



        // Initialize views
        tripNameTextView= findViewById(R.id.bills_header)
        val addBillButton : Button = findViewById(R.id.add_bill_button)
        totalBillsTextView = findViewById(R.id.total_bills_textview)
        tripDetailButtonContainer = findViewById(R.id.tripDetail_button_container) // Add initialization

        val tripName = intent.getStringExtra("trip_name")
        tripNameTextView.text = tripName ?:"Trip Details: "

        val newBill = Bill() // Creating a new Bill instance with the bill name
        billsList.add(newBill)


        addBillButton.setOnClickListener {
            showBillNameDialog()
        }
    }

    private fun showBillNameDialog() {
        // Inflate the dialog view
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_bill_name, null)
        val billNameEditText = dialogView.findViewById<EditText>(R.id.bill_name_edittext)

        // Build the AlertDialog
        AlertDialog.Builder(this)
            .setTitle("Enter Bill Name")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val billName = billNameEditText.text.toString().trim()
                if (billName.isNotEmpty()) {
                    createBillButton(billName)
                } else {
                    Toast.makeText(this, "Bill name cannot be empty", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun createBillButton(billName: String) {
        // Create a new button for the bill
        val newButton = Button(this)
        newButton.text = billName
        newButton.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // OnClick: Open AddBillActivity for the selected bill
        newButton.setOnClickListener {
            val intent = Intent(this, AddBillActivity::class.java)
            intent.putExtra("bill_name", billName)
            startActivityForResult(intent, REQUEST_ADD_BILL)
        }

        // Add the button to the scrollable container
        tripDetailButtonContainer.addView(newButton)

        totalBillsCount++
        updateTotalBillsDisplay()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_BILL && resultCode == Activity.RESULT_OK && data != null) {
            val bill = data.getSerializableExtra("bill") as? Bill
            bill?.let {
                billsList.add(it) // Add the bill to the bills list
                updateTotalBillsDisplay() // Update the total bills count
            }
        }
    }

    private fun updateTotalBillsDisplay() {
        totalBillsTextView.text = "Total bills: $totalBillsCount"
    }

    companion object {
        private const val REQUEST_ADD_BILL = 1
    }

    fun BackButton(view: View?) {
        onBackPressed()
    }
}
