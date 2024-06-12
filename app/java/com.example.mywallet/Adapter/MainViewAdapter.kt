package com.example.mywallet.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mywallet.DetailActivity
import com.example.mywallet.R
import com.example.mywallet.ViewHolder.MainViewHolder
import com.example.mywallet.data.model.ListItem


class MainViewAdapter(private val data: MutableList<ListItem>): RecyclerView.Adapter<MainViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        if (data[position].flg){
            holder.amount.text = data[position].amount
            holder.date.text = data[position].updateTime
            holder.confidenceType.text = data[position].confidenceType

            if (data[position].sendOrReceive.equals("send")){
                holder.sendOrReceive.text = "送金"
            }else if (data[position].sendOrReceive.equals("receive")){
                holder.sendOrReceive.text = "受取"
            }

            holder.cardView.setOnClickListener { v ->
                    val intent = Intent(v.context, DetailActivity::class.java)
                    intent.putExtra("txid", data[position].txid)
                    intent.putExtra("transaction", data[position].transaction)
                    intent.putExtra("sendOrReceive", data[position].sendOrReceive)
                    intent.putExtra("sendOrReceiveAddress", data[position].sendOrReceiveAddress)
                    intent.putExtra("updateTime", data[position].updateTime)
                    intent.putExtra("confidenceType", data[position].confidenceType)
                    intent.putExtra("amount", data[position].amount)
                    intent.putExtra("fee", data[position].fee)
                    v.context.startActivity(intent)
            }
        }else {
            holder.date.text = "取引はありません"
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }
}
