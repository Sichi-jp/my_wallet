package com.example.mywallet.startup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mywallet.MainActivity
import com.example.mywallet.R
import java.io.File

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (File("/data/data/com.example.mywallet/files/test-wallet.wallet").exists()){
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }else{
            setContentView(R.layout.startup)
        }
    }
}
