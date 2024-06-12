package com.example.mywallet

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mywallet.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.bitcoinj.core.listeners.DownloadProgressTracker
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.params.TestNet3Params
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var params = TestNet3Params.get()
    val kit = WalletAppKit(params, File("/data/data/com.example.mywallet/files/"), "test-wallet")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

        kit.setDownloadListener(object : DownloadProgressTracker() {
            /*
            override fun progress(pct: Double, blocksSoFar: Int, date: Date?) {
                super.progress(pct, blocksSoFar, date)
                val percentage = pct.toInt()
                Log.d("progress", percentage.toString())
            }
            */
            override fun doneDownload() {
                super.doneDownload()
                Log.d("finish", "download finish")
            }
        })

        kit.setAutoSave(true)
        kit.setBlockingStartup(false)
        kit.startAsync()
        kit.awaitRunning()
    }


    override fun onDestroy() {
        super.onDestroy()
        kit.stopAsync()
    }
}
