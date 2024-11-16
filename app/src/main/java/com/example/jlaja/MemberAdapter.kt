package com.example.jlaja

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MembersAdapter(
    private val members: List<String>,            // List of member names
    private val memberAmounts: Map<String, Double> // Map to track each member's owed amount
) : RecyclerView.Adapter<MembersAdapter.MemberViewHolder>() {

    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val memberShareTextView: TextView = itemView.findViewById(R.id.member_share_textview)

        // Bind member name and their current owed amount
        fun bind(memberName: String, amountOwed: Double) {
            memberShareTextView.text = "$memberName's current bill: $${"%.2f".format(amountOwed)}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.member_item, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val memberName = members[position]
        val amountOwed = memberAmounts[memberName] ?: 0.0 // Get the current owed amount, default to 0.0 if not found
        holder.bind(memberName, amountOwed)
    }

    override fun getItemCount(): Int = members.size
}
