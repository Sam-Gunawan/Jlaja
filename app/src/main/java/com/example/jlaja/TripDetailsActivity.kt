package com.example.jlaja

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class TripDetailsActivity : AppCompatActivity() {

    private lateinit var billsRecyclerView: RecyclerView
    private lateinit var addBillButton: Button
    private lateinit var tripNameTextView: TextView
    private lateinit var totalBillsTextView: TextView
    private lateinit var tripDetailButtonContainer: LinearLayout

    private val billsList = mutableListOf<String>() // List to store bill names
    private var totalBillsCount = 0
    private val fileName = "bills.json" // File name to save bill data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_detail)

        tripNameTextView = findViewById(R.id.bills_header)
        addBillButton = findViewById(R.id.add_bill_button)
        totalBillsTextView = findViewById(R.id.total_bills_textview)
        tripDetailButtonContainer = findViewById(R.id.tripDetail_button_container)

        val tripName = intent.getStringExtra("trip_name")
        tripNameTextView.text = tripName ?: "Trip Details: "

        // Load saved bills and recreate buttons
        loadBillsFromFile()?.let { savedBills ->
            billsList.addAll(savedBills)
            savedBills.forEach { billName ->
                createBillButton(billName)
            }
        }

        addBillButton.setOnClickListener {
            showBillNameDialog()
        }
    }

    private fun showBillNameDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_bill_name, null)
        val billNameEditText = dialogView.findViewById<EditText>(R.id.bill_name_edittext)

        AlertDialog.Builder(this)
            .setTitle("Enter Bill Name")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val billName = billNameEditText.text.toString().trim()
                if (billName.isNotEmpty()) {
                    billsList.add(billName) // Add to the list
                    saveBillsToFile() // Save updated list to file
                    createBillButton(billName) // Create a button for the new bill
                } else {
                    Toast.makeText(this, "Bill name cannot be empty", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun createBillButton(billName: String) {
        val newButton = Button(this)
        newButton.text = billName
        newButton.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        newButton.setOnClickListener {
            val intent = Intent(this, AddBillActivity::class.java)
            intent.putExtra("bill_name", billName)
            startActivityForResult(intent, REQUEST_ADD_BILL)
        }

        tripDetailButtonContainer.addView(newButton)

        totalBillsCount++
        updateTotalBillsDisplay()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_BILL && resultCode == Activity.RESULT_OK && data != null) {
            val bill = data.getStringExtra("bill_name")
            bill?.let {
                billsList.add(it) // Add the bill to the bills list
                saveBillsToFile() // Save updated list to file
                updateTotalBillsDisplay() // Update the total bills count
            }
        }
    }

    private fun updateTotalBillsDisplay() {
        totalBillsTextView.text = "Total bills: $totalBillsCount"
    }

    // Save bills to a JSON file and log the file path
    private fun saveBillsToFile() {
        val gson = Gson()
        val jsonString = gson.toJson(billsList)
        val file = File(filesDir, fileName)
        file.writeText(jsonString)

        // Log the file path
        Log.d("TripDetailsActivity", "Bills saved to file: ${file.absolutePath}")
    }

    // Load bills from a JSON file
    private fun loadBillsFromFile(): List<String>? {
        val file = File(filesDir, fileName)
        if (!file.exists()) return null

        val jsonString = file.readText()
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(jsonString, type)
    }

    companion object {
        private const val REQUEST_ADD_BILL = 1
    }

    fun toChatViewModel(view: View?) {
        val intent = Intent(
            this@TripDetailsActivity,
            GeminiChatbot::class.java
        )
        startActivity(intent)
    }

    fun BackButton(view: View?) {
        onBackPressed()
    }

    fun toChat(view: View) {
        val intent = Intent(
            this@TripDetailsActivity,
            ChatRoom::class.java
        )
        startActivity(intent)
    }

    fun tochecklist2(view: View?) {
        val intent = Intent(
            this@TripDetailsActivity,
            checklist2::class.java
        )
        startActivity(intent)
    }
}