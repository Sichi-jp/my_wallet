package com.example.mywallet

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

import java.time.LocalDateTime

class DownloadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fileName = "Tx_download" + LocalDateTime.now() + ".csv"
        val csvContent = intent.getStringExtra("csv")

        val startForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK){
                result.data?.data?.let {
                    contentResolver.openOutputStream(it)?.bufferedWriter()?.use {
                        it.write(csvContent)
                    }
                }
            }
            finish()
        }

        startForResult.launch(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            putExtra(Intent.EXTRA_TITLE, fileName)
            type = "text/csv"
        })
    }
}
