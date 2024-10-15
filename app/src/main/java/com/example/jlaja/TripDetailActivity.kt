package com.example.jlaja

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TripDetailsActivity : AppCompatActivity() {

    private lateinit var tripNameTextView: TextView
    private lateinit var billsRecyclerView: RecyclerView
    private lateinit var addBillButton: Button
    private var billsList = mutableListOf<String>() // For simplicity, we're using a string list for bills

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_detail)

        // Initialize views
        tripNameTextView = findViewById(R.id.bills_header)
        billsRecyclerView = findViewById(R.id.bills_recycler_view)
        addBillButton = findViewById(R.id.add_bill_button)

        // Get the trip name from the Intent and set it in the header
        val tripName = intent.getStringExtra("trip_name")
        tripNameTextView.text = tripName ?: "Trip Details: "

        // Set up RecyclerView for the bills
        billsRecyclerView.layoutManager = LinearLayoutManager(this)
        val billsAdapter = BillsAdapter(billsList)
        billsRecyclerView.adapter = billsAdapter

        // Add a new bill button click
        addBillButton.setOnClickListener {
            val intent = Intent(this, AddBillActivity::class.java)
            intent.putExtra("trip_name", tripName) // Pass trip name to the AddBillActivity
            startActivityForResult(intent, ADD_BILL_REQUEST_CODE)
        }
    }

    // Handle result from AddBillActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_BILL_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val billName = data.getStringExtra("bill_name")
            val billAmount = data.getStringExtra("bill_amount")
            val paidBy = data.getStringExtra("paid_by")
            val membersList = data.getStringArrayListExtra("members")

            // Update the UI to display the bill summary
            updateBillSummary(billName, billAmount, paidBy, membersList)
        }
    }

    private fun updateBillSummary(billName: String?, billAmount: String?, paidBy: String?, membersList: ArrayList<String>?) {
        // Update the TextViews with the bill data and make them visible
        findViewById<TextView>(R.id.bill_name_textview).apply {
            text = "Bill Name: $billName"
            visibility = View.VISIBLE
        }

        findViewById<TextView>(R.id.bill_amount_textview).apply {
            text = "Bill Amount: $billAmount"
            visibility = View.VISIBLE
        }

        findViewById<TextView>(R.id.paid_by_textview).apply {
            text = "Paid By: $paidBy"
            visibility = View.VISIBLE
        }

        // Update RecyclerView for members and make it visible
        val membersRecyclerView: RecyclerView = findViewById(R.id.members_recycler_view)
        val membersAdapter = MembersAdapter(membersList ?: arrayListOf())
        membersRecyclerView.adapter = membersAdapter
        membersRecyclerView.visibility = View.VISIBLE
    }

    companion object {
        const val ADD_BILL_REQUEST_CODE = 1
    }
}
