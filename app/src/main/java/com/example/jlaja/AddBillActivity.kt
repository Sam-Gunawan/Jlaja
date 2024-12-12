package com.example.jlaja

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

data class Item(val name: String, val unitPrice: Double, val memberPortions: MutableMap<String, Int> = mutableMapOf()) : Serializable


class AddBillActivity : AppCompatActivity() {

    private lateinit var billNameTextView: TextView
    private lateinit var billAmountTextView: TextView
    private lateinit var paidBySpinner: Spinner
    private lateinit var membersRecyclerView: RecyclerView
    private lateinit var membersAdapter: MembersAdapter
    private lateinit var saveBillButton: Button
    private lateinit var addItemButton: Button
    private lateinit var itemsRecyclerView: RecyclerView
    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var spinnerAdapter: ArrayAdapter<String>

    private val membersList = mutableListOf<String>()
    private val memberAmounts = mutableMapOf<String, Double>() // Track each member's current amount
    private val itemsList = mutableListOf<Item>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bill)

        billNameTextView = findViewById(R.id.bill_name_textview)
        billAmountTextView = findViewById(R.id.bill_amount_textview)
        paidBySpinner = findViewById(R.id.paid_by_spinner)
        membersRecyclerView = findViewById(R.id.members_recycler_view)
        saveBillButton = findViewById(R.id.save_bill_button)
        addItemButton = findViewById(R.id.add_item_button)
        itemsRecyclerView = findViewById(R.id.items_recycler_view)

        membersRecyclerView.layoutManager = LinearLayoutManager(this)
        membersAdapter = MembersAdapter(membersList, memberAmounts)
        membersRecyclerView.adapter = membersAdapter

        itemsRecyclerView.layoutManager = LinearLayoutManager(this)
        itemsAdapter = ItemsAdapter(itemsList)
        itemsRecyclerView.adapter = itemsAdapter

        spinnerAdapter = ArrayAdapter(this@AddBillActivity, android.R.layout.simple_spinner_item, membersList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        paidBySpinner.adapter = spinnerAdapter

        val billName = intent.getStringExtra("bill_name")
        billNameTextView.text = billName ?: "Unknown Bill"

        val addMembersButton: Button = findViewById(R.id.button2)
        addMembersButton.setOnClickListener {
            showAddMemberDialog()
        }

        loadSavedBill()

        addItemButton.setOnClickListener {
            showAddItemDialog()
        }

        saveBillButton.setOnClickListener {
            saveBill()
        }
    }

    private fun calculateTotalAmount(): Double {
        // Calculate the total amount based on all items
        return itemsList.sumOf { it.unitPrice }
    }

    private fun calculateMemberShare(member: String): Double {
        var totalShare = 0.0

        // Calculate how much this member should pay based on portions
        for (item in itemsList) {
            val totalPortions = item.memberPortions.values.sum()  // Total portions for this item
            val memberPortion = item.memberPortions[member] ?: 0   // Portion for the current member

            // Calculate member's share for this item
            if (totalPortions > 0) {
                totalShare += (memberPortion.toDouble() / totalPortions) * item.unitPrice
            }
        }

        return totalShare
    }

    private fun updateMemberAmounts() {
        // Recalculate each member's amount and update the display
        membersList.forEach { member ->
            memberAmounts[member] = calculateMemberShare(member)
        }
        membersAdapter.notifyDataSetChanged() // Refresh the members display
    }

    private fun showAddItemDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_item, null)
        val itemNameEditText = dialogView.findViewById<EditText>(R.id.item_name_edittext)
        val itemPriceEditText = dialogView.findViewById<EditText>(R.id.item_price_edittext)

        AlertDialog.Builder(this)
            .setTitle("Enter Item Details")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val itemName = itemNameEditText.text.toString().trim()
                val itemPrice = itemPriceEditText.text.toString().toDoubleOrNull() ?: 0.0

                if (itemName.isNotEmpty() && itemPrice > 0) {
                    val item = Item(itemName, itemPrice)
                    itemsList.add(item)
                    itemsAdapter.notifyItemInserted(itemsList.size - 1)

                    // Show the portion assignment dialog for the newly added item
                    assignPortionsDialog(item)

                    // Update the total amount displayed and member amounts
                    updateTotalAmountDisplay()
                    updateMemberAmounts()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun updateTotalAmountDisplay() {
        val totalAmount = calculateTotalAmount()
        billAmountTextView.text = String.format("%.2f", totalAmount)
    }

    private fun assignPortionsDialog(item: Item) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_portion_assignment, null)
        val portionsContainer = dialogView.findViewById<LinearLayout>(R.id.portions_container)

        // Dynamically add portion input for each member
        membersList.forEach { member ->
            val memberRow = LayoutInflater.from(this).inflate(R.layout.portion_item, portionsContainer, false)
            val memberNameText = memberRow.findViewById<TextView>(R.id.member_name_textview)
            val portionEditText = memberRow.findViewById<EditText>(R.id.portion_edittext)

            memberNameText.text = member
            portionsContainer.addView(memberRow)
        }

        AlertDialog.Builder(this)
            .setTitle("Assign Portions for ${item.name}")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                // Save the portion data for each member
                for (i in 0 until portionsContainer.childCount) {
                    val row = portionsContainer.getChildAt(i)
                    val memberName = membersList[i]
                    val portionText = row.findViewById<EditText>(R.id.portion_edittext).text.toString()
                    val portion = portionText.toIntOrNull() ?: 0

                    // Update the memberPortions map for this item
                    item.memberPortions[memberName] = portion
                }

                // Refresh the item list view
                itemsAdapter.notifyDataSetChanged()

                // Update member amounts after assigning portions
                updateMemberAmounts()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun saveBill() {
        val billName = billNameTextView.text.toString().trim()
        val paidBy = paidBySpinner.selectedItem?.toString() ?: ""

        if (billName.isNotEmpty()) {
            val totalAmount = calculateTotalAmount()

            // Calculate member shares based on the updated memberPortions for each item
            val memberShares = membersList.associateWith { member ->
                calculateMemberShare(member)
            }

            // Create the Bill object with updated memberShares
            val bill = Bill(
                name = billName,
                amount = totalAmount,
                totalAmount = totalAmount,
                paidBy = paidBy,
                memberShares = memberShares, // Updated memberShares map
                itemsList = itemsList
            )

            // Save the bill to a file
            val fileName = "bill_${System.currentTimeMillis()}.dat"
            val file = File(filesDir, fileName)
            try {
                ObjectOutputStream(file.outputStream()).use { it.writeObject(bill) }
                Log.d("SaveBill", "Bill saved locally at: ${file.absolutePath}")
                Toast.makeText(this, "Bill saved successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("SaveBill", "Error saving bill: ${e.message}")
                Toast.makeText(this, "Error saving bill!", Toast.LENGTH_SHORT).show()
            }

            // Return the bill details to the previous activity
            val resultIntent = Intent()
            resultIntent.putExtra("bill", bill) // Serialize the Bill object
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        } else {
            Toast.makeText(this, "Bill name cannot be empty!", Toast.LENGTH_SHORT).show()
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
                    memberAmounts[memberName] = 0.0 // Initialize member's share to zero
                    membersAdapter.notifyItemInserted(membersList.size - 1)

                    // Update PaidBy Spinner
                    spinnerAdapter.notifyDataSetChanged()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun loadSavedBill() {
        val savedBillFile = File(filesDir, "bill_${System.currentTimeMillis()}.dat") // Or retrieve file name from saved preferences
        if (savedBillFile.exists()) {
            try {
                ObjectInputStream(savedBillFile.inputStream()).use { stream ->
                    val savedBill = stream.readObject() as Bill
                    // Restore the saved data to your UI
                    billNameTextView.text = savedBill.name
                    billAmountTextView.text = savedBill.amount.toString()
                    membersList.clear()
                    membersList.addAll(savedBill.memberShares.keys)
                    itemsList.clear()
                    itemsList.addAll(savedBill.itemsList)

                    // Update adapters with the restored data
                    membersAdapter.notifyDataSetChanged()
                    itemsAdapter.notifyDataSetChanged()

                    updateMemberAmounts()
                    updateTotalAmountDisplay()
                }
            } catch (e: Exception) {
                Log.e("LoadBill", "Error loading bill: ${e.message}")
            }
        }
    }
    fun BackButton(view: View?) {
        onBackPressed()
    }
}