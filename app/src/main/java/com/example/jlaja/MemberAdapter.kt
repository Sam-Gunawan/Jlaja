package com.example.jlaja

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MembersAdapter(private val members: MutableList<String>) : RecyclerView.Adapter<MembersAdapter.MemberViewHolder>() {

    // ViewHolder class
    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val memberNameTextView: TextView = itemView.findViewById(R.id.member_name_text_view) // Assuming this TextView is in member_item.xml

        // Binds the member name to the TextView
        fun bind(memberName: String) {
            memberNameTextView.text = memberName
        }
    }

    // Inflates the item view for each member
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.member_item, parent, false) // Make sure this is the correct layout
        return MemberViewHolder(view)
    }

    // Binds the data to the ViewHolder
    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(members[position])
    }

    // Returns the size of the member list
    override fun getItemCount(): Int = members.size
}
