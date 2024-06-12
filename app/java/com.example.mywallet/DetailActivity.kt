package com.example.mywallet

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail)

        val txid = intent.getStringExtra("txid")
        val amount = intent.getStringExtra("amount")
        val fee = intent.getStringExtra("fee")
        val sendOrReceive = intent.getStringExtra("sendOrReceive")
        val sendOrReceiveAddress = intent.getStringExtra("sendOrReceiveAddress")
        val updateTime = intent.getStringExtra("updateTime")
        val confidenceType = intent.getStringExtra("confidenceType")
        val transaction = intent.getStringExtra("transaction")

        findViewById<TextView>(R.id.txid).text = "TransactionID：" + txid
        findViewById<TextView>(R.id.updateTime).text = "更新時間：" + updateTime
        findViewById<TextView>(R.id.amount).text = "金額：" + amount + "　" + sendOrReceive
        findViewById<TextView>(R.id.fee).text = "手数料：" + fee
        findViewById<TextView>(R.id.confidenceType).text = "状態：" + confidenceType
        findViewById<TextView>(R.id.forAddress).text = "相手アドレス：" + sendOrReceiveAddress
        findViewById<TextView>(R.id.tx).text = "トランザクション： \n" + transaction

    }

}
