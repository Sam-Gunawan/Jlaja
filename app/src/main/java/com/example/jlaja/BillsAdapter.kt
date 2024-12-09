package com.example.jlaja

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable

// Updated Bill data class with memberShares as a Map
data class Bill(
    val name: String = "default bill",
    val amount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val paidBy: String = "ye",
    val memberShares: Map<String, Double> = emptyMap(), // Member shares map
    val itemsList: List<Item> = emptyList()
) : Serializable

class BillsAdapter(private val bills: List<Bill>) : RecyclerView.Adapter<BillsAdapter.BillViewHolder>() {

    // ViewHolder class to hold references to the views for each item
    class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val billNameTextView: TextView = itemView.findViewById(R.id.bill_name_textview)
        private val totalAmountTextView: TextView = itemView.findViewById(R.id.total_amount_textview)
        private val paidByTextView: TextView = itemView.findViewById(R.id.paid_by_textview)
        private val membersSharesContainer: LinearLayout = itemView.findViewById(R.id.members_shares_container)


        // Bind the bill details to the views
        fun bind(bill: Bill) {
            // Set the bill information
            billNameTextView.text = bill.name
            totalAmountTextView.text = "Total amount: $${"%.2f".format(bill.amount)}"
            paidByTextView.text = "Paid By: ${bill.paidBy}"

            // Clear any previous member share views to avoid duplication
            membersSharesContainer.removeAllViews()

            // Dynamically create and add views for each member's share
            bill.memberShares.forEach { (memberName, shareAmount) ->
                val memberShareView = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.item_member_share, membersSharesContainer, false)

                // Find the TextViews in the member share view and set the data
                val memberNameTextView = memberShareView.findViewById<TextView>(R.id.member_name_textview)
                val memberShareTextView = memberShareView.findViewById<TextView>(R.id.member_share_textview)

                // Set the member's name and share amount
                memberNameTextView.text = memberName
                memberShareTextView.text = "Owes: $${"%.2f".format(shareAmount)}"




                // Add the view to the members' container layout
                membersSharesContainer.addView(memberShareView)
            }
        }
    }

    // Create a new view for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bill, parent, false)
        return BillViewHolder(view)
    }

    // Bind the data to each view item
    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        holder.bind(bills[position])
    }

    // Return the total number of items
    override fun getItemCount(): Int = bills.size
}
