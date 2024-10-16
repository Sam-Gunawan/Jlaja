package com.example.jlaja

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adapter class for RecyclerView to display the list of bills
class BillsAdapter(private val billsList: List<String>) : RecyclerView.Adapter<BillsAdapter.BillViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bill, parent, false)
        return BillViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        holder.bind(billsList[position])
    }

    override fun getItemCount(): Int = billsList.size

    class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val billTextView: TextView = itemView.findViewById(R.id.bill_textview)

        fun bind(billDetails: String) {
            billTextView.text = billDetails
        }
    }
}