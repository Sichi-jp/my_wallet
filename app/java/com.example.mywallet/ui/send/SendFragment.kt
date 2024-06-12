package com.example.mywallet.ui.send

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mywallet.MainActivity
import com.example.mywallet.R
import com.example.mywallet.databinding.SendBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import org.bitcoinj.core.Address
import org.bitcoinj.core.Coin
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.uri.BitcoinURI
import java.util.concurrent.Executors


class SendFragment : Fragment() {
    private var _binding: SendBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SendBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //QRコードをリーダ設定
        val barcodeLauncher = registerForActivityResult(
            ScanContract()
        ) { result ->
            if (result.contents !== null) {
                //QRコードを読み取れた場合
                val uri = result.contents
                if (uri.startsWith("http")) {
                    // URL may serve either HTML or a payment request depending on how it's fetched.
                    // Try here to get a payment request.
                    Toast.makeText(context, "このURLは読み込めません", Toast.LENGTH_LONG).show();
                } else if (uri.startsWith("bitcoin:")) {
                    val requestUri = BitcoinURI(uri)
                    if (requestUri.address !== null){
                        binding.sendAddress.setText(requestUri.address.toString())
                    }
                    if (requestUri.amount !== null){
                        binding.sendAmount.setText(requestUri.amount.toPlainString())
                    }
                }else{
                    Toast.makeText(context, "このURLは読み込めません", Toast.LENGTH_LONG).show();
                }
            }
        }

        binding.cameraButton.setOnClickListener{
            //QRコードリーダー立ち上げ
            val options = ScanOptions().setOrientationLocked(false)
            barcodeLauncher.launch(options)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as MainActivity
        val kit = mainActivity.kit


        val executor = Executors.newSingleThreadExecutor()
        // Runnableオブジェクトを定義
        val bloadcastRunnable = Runnable {
            try {
                Log.d("BloadCastRunnable", "BloadCast completed")
            }catch (e: Exception){
                Log.d("BloadCastRunnable", "BloadCast completed but occured Error")
            }
        }

        binding.imageButton.setOnClickListener {
            val sendAmount = binding.sendAmount.text.toString().toBigDecimal()
            val sendBtc = Coin.ofBtc(sendAmount)
            val sendAddress = Address.fromString(TestNet3Params.get(), binding.sendAddress.text.toString())

            try {
                val result = kit.wallet().sendCoins(kit.peerGroup(), sendAddress, sendBtc)
                result.broadcastComplete.addListener(bloadcastRunnable, executor)
                findNavController().navigate(R.id.action_send_to_home)
            }catch (e : Exception){
                Toast.makeText(requireContext(), "十分な金額がありません。", Toast.LENGTH_SHORT).show()
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
