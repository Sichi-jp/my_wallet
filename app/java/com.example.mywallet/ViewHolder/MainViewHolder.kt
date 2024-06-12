package com.example.mywallet.ViewHolder

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.mywallet.R

class MainViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val cardView = itemView.findViewById<CardView>(R.id.cardView)
    val amount = itemView.findViewById<TextView>(R.id.amount)
    val sendOrReceive = itemView.findViewById<TextView>(R.id.sendOrReceive)
    val date = itemView.findViewById<TextView>(R.id.date)
    val confidenceType = itemView.findViewById<TextView>(R.id.confidenceType)
}
