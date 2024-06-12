package com.example.mywallet.data.model

import java.io.Serializable

data class ListItem(
    val txid : String,
    val amount: String,
    val fee: String,
    val sendOrReceive: String,
    val sendOrReceiveAddress: String,
    val updateTime: String,
    val confidenceType: String,
    val transaction: String,
    val flg : Boolean,
) : Serializable
