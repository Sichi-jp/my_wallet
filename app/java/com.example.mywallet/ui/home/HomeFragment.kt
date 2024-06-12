package com.example.mywallet.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mywallet.MainActivity
import com.example.mywallet.R
import com.example.mywallet.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.net.URL


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.receiveBtn.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_receive)
        }

        binding.sendBtn.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_send)
        }


        return root
    }

    override fun onStart() {
        super.onStart()
        binding.marketCapitalization.text = "取得中..."
        val mainActivity = activity as MainActivity
        val kit = mainActivity.kit
        binding.balance.text = kit.wallet().balance.toFriendlyString()
        val balance = kit.wallet().balance.toPlainString()

        lifecycleScope.launch {
            //BTCの時価をapiで取得、表示
            val cm = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            if (capabilities !== null){
                val result = processBackground()
                try {
                    postBackground(result, balance)
                }catch (e: Exception){
                    Log.d("Error", e.toString())
                }
            }else{
                Toast.makeText(requireContext(), "ネットワークに接続してください", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @WorkerThread
    private suspend fun processBackground() : Int{
        return withContext(Dispatchers.IO){
            var result = 0
            val client = OkHttpClient()
            val url = URL("https://api.bitflyer.com/v1/ticker")
            //val url = URL("https://bitflyer.com/api/echo/price")
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful){
                    throw IOException("Unexpected code $response")
                }

                val res = response.body!!.string()
                val jsonData = JSONObject(res)
                result = jsonData.getInt("ltp")
            }
            result
        }

    }
    @UiThread
    private suspend fun postBackground(result: Int, balance: String){
        val res = result.toBigDecimal() * balance.toBigDecimal()
        binding.marketCapitalization.text = "約" + res.toInt().toString() + "円"
    }
}
