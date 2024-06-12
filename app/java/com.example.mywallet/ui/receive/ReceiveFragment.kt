package com.example.mywallet.ui.receive

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mywallet.MainActivity
import com.example.mywallet.databinding.ReceiveBinding
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import org.bitcoinj.core.Coin
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.uri.BitcoinURI.convertToBitcoinURI


class ReceiveFragment : Fragment() {
    private var _binding: ReceiveBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ReceiveBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val mainActivity = activity as MainActivity
        val kit = mainActivity.kit
        val receive_address = kit.wallet().currentReceiveAddress().toString()
        binding.receiveAddress.text = receive_address

        val barcodeEncoder = BarcodeEncoder()
        //初期表示の金額なしURL
        var uri = convertToBitcoinURI(TestNet3Params.get(), receive_address, null, null, null)
        var bitmap = barcodeEncoder.encodeBitmap(uri.toString(), BarcodeFormat.QR_CODE, 250, 250)
        binding.imageView.setImageBitmap(bitmap)

        binding.imageReload.setOnClickListener {
            if (!TextUtils.isEmpty(binding.textInputLayout.editText?.text)){
                //金額付きのURL
                val amount = binding.textInputLayout.editText?.text.toString().toBigDecimal()
                uri = convertToBitcoinURI(TestNet3Params.get(), receive_address, Coin.ofBtc(amount), null, null)
                bitmap = barcodeEncoder.encodeBitmap(uri.toString(), BarcodeFormat.QR_CODE, 250, 250)
                binding.imageView.setImageBitmap(bitmap)
                Toast.makeText(context, "QRコードを更新しました", Toast.LENGTH_SHORT).show()

            }else{
                //金額なしのURL
                uri = convertToBitcoinURI(TestNet3Params.get(), receive_address, null, null, null)
                bitmap = barcodeEncoder.encodeBitmap(uri.toString(), BarcodeFormat.QR_CODE, 250, 250)
                binding.imageView.setImageBitmap(bitmap)
                Toast.makeText(context, "QRコードを更新しました", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
