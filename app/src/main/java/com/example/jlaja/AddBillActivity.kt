package com.example.jlaja

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AddBillActivity : AppCompatActivity() {

    private lateinit var billNameEditText: EditText
    private lateinit var billAmountEditText: EditText
    private lateinit var paidBySpinner: Spinner
    private lateinit var membersRecyclerView: RecyclerView
    private lateinit var membersAdapter: MembersAdapter
    private lateinit var saveBillButton: Button

    private val membersList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bill)

        // Initialize views
        billNameEditText = findViewById(R.id.bill_name_edittext)
        billAmountEditText = findViewById(R.id.bill_amount_edittext)
        paidBySpinner = findViewById(R.id.paid_by_spinner)
        membersRecyclerView = findViewById(R.id.members_recycler_view)
        saveBillButton = findViewById(R.id.save_bill_button)

        // Set up RecyclerView
        membersRecyclerView.layoutManager = LinearLayoutManager(this)
        membersAdapter = MembersAdapter(membersList)
        membersRecyclerView.adapter = membersAdapter

        // Button to add members
        val addMembersButton: Button = findViewById(R.id.button2)
        addMembersButton.setOnClickListener {
            showAddMemberDialog()
        }

        // Set up save button click listener
        saveBillButton.setOnClickListener {
            saveBill()
        }
    }

    private fun saveBill() {
        val billName = billNameEditText.text.toString().trim()
        val billAmount = billAmountEditText.text.toString().trim()
        val paidBy = paidBySpinner.selectedItem.toString()

        if (billName.isNotEmpty() && billAmount.isNotEmpty()) {
            // Return the bill details to TripDetailsActivity
            val resultIntent = Intent()
            resultIntent.putExtra("bill_name", billName)
            resultIntent.putExtra("bill_amount", billAmount)
            resultIntent.putExtra("paid_by", paidBy)
            resultIntent.putStringArrayListExtra("members", ArrayList(membersList))
            setResult(Activity.RESULT_OK, resultIntent)
            finish() // Close the activity
        }
    }

    private fun showAddMemberDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_member, null)
        val memberNameEditText = dialogView.findViewById<EditText>(R.id.member_name_edittext)

        AlertDialog.Builder(this)
            .setTitle("Enter Member Name")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val memberName = memberNameEditText.text.toString().trim()
                if (memberName.isNotEmpty()) {
                    membersList.add(memberName)
                    membersAdapter.notifyItemInserted(membersList.size - 1)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
