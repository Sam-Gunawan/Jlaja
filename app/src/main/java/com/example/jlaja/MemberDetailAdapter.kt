package com.example.jlaja

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MemberDetailsAdapter {
    class MemberDetailsAdapter(private val memberShares: Map<String, Double>) : RecyclerView.Adapter<MemberDetailsAdapter.MemberDetailViewHolder>() {

        class MemberDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val memberNameTextView: TextView = itemView.findViewById(R.id.member_name_textview)
            private val memberShareTextView: TextView = itemView.findViewById(R.id.member_share_textview)

            fun bind(memberName: String, shareAmount: Double) {
                memberNameTextView.text = memberName
                memberShareTextView.text = "Owes: $${"%.2f".format(shareAmount)}"
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberDetailViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_member_share, parent, false)
            return MemberDetailViewHolder(view)
        }

        override fun onBindViewHolder(holder: MemberDetailViewHolder, position: Int) {
            val memberName = memberShares.keys.toList()[position]
            val shareAmount = memberShares[memberName] ?: 0.0
            holder.bind(memberName, shareAmount)
        }

        override fun getItemCount(): Int = memberShares.size
    }
}