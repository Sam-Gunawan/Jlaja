package com.example.jlaja

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adapter class for RecyclerView to display the list of bills
class BillsAdapter(private val billsList: List<String>) :
    RecyclerView.Adapter<BillsAdapter.BillViewHolder>() {

    // ViewHolder to represent each bill item in the RecyclerView
    class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val billNameTextView: TextView = itemView.findViewById(R.id.bill_name_textview)
    }

    // Inflate the item layout for each bill
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bill, parent, false)
        return BillViewHolder(view)
    }

    // Bind data to each item in the RecyclerView
    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val bill = billsList[position]
        holder.billNameTextView.text = bill
    }

    // Get the number of bills in the list
    override fun getItemCount(): Int {
        return billsList.size
    }
}
