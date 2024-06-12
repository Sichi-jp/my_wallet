package com.example.mywallet.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mywallet.Adapter.MainViewAdapter
import com.example.mywallet.DownloadActivity
import com.example.mywallet.MainActivity
import com.example.mywallet.data.model.ListItem
import com.example.mywallet.databinding.FragmentDashboardBinding
import com.example.mywallet.ui.numberPicker.NumberPickerDialog
import org.bitcoinj.wallet.WalletTransaction
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class DashboardFragment : Fragment(), NumberPickerDialog.NoticeDialogListener {
    private var trans_lists = mutableListOf<ListItem>()
    override fun onNumberPickerDialogNegativeClick(dialog: DialogFragment) {
        return
    }

    override fun onNumberPickerDialogPositiveClick(
        dialog: DialogFragment,
        selectedYearItem: Int,
        selectedMonthItem: Int
    ){
        //ここに受け取ったint型の年月をdate型にしてwalletのtransactionを出力
        val inputDate = LocalDate.of(selectedYearItem, selectedMonthItem, 1)
        val mainActivity = activity as MainActivity
        val kit = mainActivity.kit
        val transactions: List<WalletTransaction> = kit.wallet().walletTransactions.sortedByDescending { it.transaction.updateTime.time }
        val formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss OOOO uuuu" , Locale.US)
        val rv = binding.recycleView
        //リストを空にする
        trans_lists = mutableListOf<ListItem>()
        var amount = ""
        var fee = ""
        var sendOrReceive = ""
        var sendOrReceiveAddress = ""

        if (!transactions.isNullOrEmpty()){
            transactions.forEach { i ->
                val updatetime = ZonedDateTime.parse(i.transaction.updateTime.toString(), formatter).toLocalDate()
                if(inputDate.month == updatetime.month && inputDate.year == updatetime.year){
                    //データを成形
                    if (i.transaction.getValueSentFromMe(kit.wallet()).isZero){
                        amount = i.transaction.getValueSentToMe(kit.wallet()).toFriendlyString()
                        fee = "負担なし"
                        sendOrReceive = "receive"
                        sendOrReceiveAddress = i.transaction.inputs.toString()
                    }else{
                        amount = (i.transaction.getValueSentFromMe(kit.wallet()) - i.transaction.getValueSentToMe(kit.wallet()) - i.transaction.fee).toFriendlyString()
                        fee = i.transaction.fee.toFriendlyString()
                        sendOrReceive = "send"
                        sendOrReceiveAddress = i.transaction.outputs.toString()
                    }
                    trans_lists.add(ListItem(
                        i.transaction.txId.toString(),
                        amount,
                        fee,
                        sendOrReceive,
                        sendOrReceiveAddress,
                        i.transaction.updateTime.toString(),
                        i.transaction.confidence.confidenceType.name,
                        i.transaction.toString(),
                        true,
                    ))
                }
            }
            //ここでrecycleviewに追加
            if (!trans_lists.isNullOrEmpty()){
                rv.setHasFixedSize(true)
                rv.layoutManager = LinearLayoutManager(requireContext()).apply {
                    orientation = LinearLayoutManager.VERTICAL
                }
                rv.adapter = MainViewAdapter(trans_lists)
            }else{
                //トーストで取引がないことを表示
                Toast.makeText(requireContext(), "指定された年月の取引はありません", Toast.LENGTH_LONG).show()
                trans_lists.add(ListItem("", "", "","", "","", "", "", false))
                rv.setHasFixedSize(true)
                rv.layoutManager = LinearLayoutManager(requireContext()).apply {
                    orientation = LinearLayoutManager.VERTICAL
                }
                rv.adapter = MainViewAdapter(trans_lists)
            }
        }else{
            //トーストで取引がないことを表示
            Toast.makeText(requireContext(), "取引はありません", Toast.LENGTH_LONG).show()
            trans_lists.add(ListItem("", "", "","", "","", "", "", false))
            rv.setHasFixedSize(true)
            rv.layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            rv.adapter = MainViewAdapter(trans_lists)
        }


    }

    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.yearMonthButton.setOnClickListener {
            val dialog = NumberPickerDialog()
            val fragment = childFragmentManager
            dialog.show(fragment, "NumberPickerDialog")
        }

        //リサイクルビューに渡す用の配列
        trans_lists = mutableListOf<ListItem>()
        //リサイクルビュー
        val rv = binding.recycleView

        val mainActivity = activity as MainActivity
        val kit = mainActivity.kit
        val transactions: List<WalletTransaction> =
            kit.wallet().walletTransactions.sortedByDescending { it.transaction.updateTime.time }
        val formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss OOOO uuuu", Locale.US)
        val today = LocalDate.now()


        binding.checkDownloadToday.setOnClickListener {
            if (binding.checkDownloadToday.isChecked) {
                //リストを空にする
                trans_lists = mutableListOf()
                var amount = ""
                var fee = ""
                var sendOrReceive = ""
                var sendOrReceiveAddress = ""

                if (!transactions.isNullOrEmpty()) {
                    transactions.forEach { i ->
                        val updatetime =
                            ZonedDateTime.parse(i.transaction.updateTime.toString(), formatter)
                                .toLocalDate()
                        if (today.equals(updatetime)) {
                            //今日なら
                            //データを成形
                            if (i.transaction.getValueSentFromMe(kit.wallet()).isZero) {
                                amount = i.transaction.getValueSentToMe(kit.wallet()).toFriendlyString()
                                fee = "負担なし"
                                sendOrReceive = "receive"
                                sendOrReceiveAddress = i.transaction.inputs.toString()
                            } else {
                                amount = (i.transaction.getValueSentFromMe(kit.wallet()) - i.transaction.getValueSentToMe(kit.wallet()) - i.transaction.fee).toFriendlyString()
                                fee = i.transaction.fee.toFriendlyString()
                                sendOrReceive = "send"
                                sendOrReceiveAddress = i.transaction.outputs.toString()
                            }

                            trans_lists.add(
                                ListItem(
                                    i.transaction.txId.toString(),
                                    amount,
                                    fee,
                                    sendOrReceive,
                                    sendOrReceiveAddress,
                                    i.transaction.updateTime.toString(),
                                    i.transaction.confidence.confidenceType.name,
                                    i.transaction.toString(),
                                    true,
                                )
                            )
                        }
                    }
                    //ここでrecycleviewに追加
                    if (!trans_lists.isNullOrEmpty()) {
                        rv.setHasFixedSize(true)
                        rv.layoutManager = LinearLayoutManager(requireContext()).apply {
                            orientation = LinearLayoutManager.VERTICAL
                        }
                        rv.adapter = MainViewAdapter(trans_lists)
                    } else {
                        //トーストで取引がないことを表示
                        Toast.makeText(requireContext(), "本日の取引はありません", Toast.LENGTH_LONG)
                            .show()
                        trans_lists.add(ListItem("", "","", "", "", "", "", "", false))
                        rv.setHasFixedSize(true)
                        rv.layoutManager = LinearLayoutManager(requireContext()).apply {
                            orientation = LinearLayoutManager.VERTICAL
                        }
                        rv.adapter = MainViewAdapter(trans_lists)
                    }
                } else {
                    //トーストで取引がないことを表示
                    Toast.makeText(requireContext(), "本日の取引はありません", Toast.LENGTH_LONG).show()
                    trans_lists.add(ListItem("", "","", "", "", "", "", "", false))
                    rv.setHasFixedSize(true)
                    rv.layoutManager = LinearLayoutManager(requireContext()).apply {
                        orientation = LinearLayoutManager.VERTICAL
                    }
                    rv.adapter = MainViewAdapter(trans_lists)
                }
            }
        }

        binding.checkDownloadAll.setOnClickListener {
            if (binding.checkDownloadAll.isChecked) {
                //リストを空にする
                trans_lists = mutableListOf()
                var amount = ""
                var fee = ""
                var sendOrReceive = ""
                var sendOrReceiveAddress = ""
                //全てのトランザクションを表示
                if (!transactions.isNullOrEmpty()) {
                    transactions.forEach { i ->
                        //データを成形
                        if (i.transaction.getValueSentFromMe(kit.wallet()).isZero) {
                            amount = i.transaction.getValueSentToMe(kit.wallet()).toFriendlyString()
                            fee = "負担なし"
                            sendOrReceive = "receive"
                            sendOrReceiveAddress = i.transaction.inputs.toString()

                        } else {
                            amount = (i.transaction.getValueSentFromMe(kit.wallet()) - i.transaction.getValueSentToMe(kit.wallet()) - i.transaction.fee).toFriendlyString()
                            fee = i.transaction.fee.toFriendlyString()
                            sendOrReceive = "send"
                            sendOrReceiveAddress = i.transaction.outputs.toString()
                        }


                        trans_lists.add(
                            ListItem(
                                i.transaction.txId.toString(),
                                amount,
                                fee,
                                sendOrReceive,
                                sendOrReceiveAddress,
                                i.transaction.updateTime.toString(),
                                i.transaction.confidence.confidenceType.name,
                                i.transaction.toString(),
                                true,
                            )
                        )
                    }
                    //recycleviewに追加
                    rv.setHasFixedSize(true)
                    rv.layoutManager = LinearLayoutManager(requireContext()).apply {
                        orientation = LinearLayoutManager.VERTICAL
                    }
                    rv.adapter = MainViewAdapter(trans_lists)
                } else {
                    //トーストで取引がないことを表示
                    Toast.makeText(requireContext(), "取引はありません", Toast.LENGTH_LONG).show()
                    trans_lists.add(ListItem("", "","", "", "", "", "", "", false))
                    rv.setHasFixedSize(true)
                    rv.layoutManager = LinearLayoutManager(requireContext()).apply {
                        orientation = LinearLayoutManager.VERTICAL
                    }
                    rv.adapter = MainViewAdapter(trans_lists)
                }

            }
        }

            //最初に今日取得
            if (!transactions.isNullOrEmpty()) {
                var amount = ""
                var fee = ""
                var sendOrReceive = ""
                var sendOrReceiveAddress = ""
                transactions.forEach { i ->
                    val updatetime =
                        ZonedDateTime.parse(i.transaction.updateTime.toString(), formatter)
                            .toLocalDate()
                    if (today.equals(updatetime)) {
                        //今日なら
                        //データを成形
                        if (i.transaction.getValueSentFromMe(kit.wallet()).isZero) {
                            amount = i.transaction.getValueSentToMe(kit.wallet()).toFriendlyString()
                            fee = "負担なし"
                            sendOrReceive = "receive"
                            sendOrReceiveAddress = i.transaction.inputs.toString()
                        } else {
                            amount = (i.transaction.getValueSentFromMe(kit.wallet()) - i.transaction.getValueSentToMe(kit.wallet()) - i.transaction.fee).toFriendlyString()
                            fee = i.transaction.fee.toFriendlyString()
                            sendOrReceive = "send"
                            sendOrReceiveAddress = i.transaction.outputs.toString()
                        }


                        trans_lists.add(
                            ListItem(
                                i.transaction.txId.toString(),
                                amount,
                                fee,
                                sendOrReceive,
                                sendOrReceiveAddress,
                                i.transaction.updateTime.toString(),
                                i.transaction.confidence.confidenceType.name,
                                i.transaction.toString(),
                                true,
                            )
                        )
                    }
                }
                //ここでrecycleviewに追加
                if (!trans_lists.isNullOrEmpty()) {
                    rv.setHasFixedSize(true)
                    rv.layoutManager = LinearLayoutManager(requireContext()).apply {
                        orientation = LinearLayoutManager.VERTICAL
                    }
                    rv.adapter = MainViewAdapter(trans_lists)
                } else {
                    //トーストで取引がないことを表示
                    Toast.makeText(requireContext(), "本日の取引はありません", Toast.LENGTH_LONG).show()
                    trans_lists.add(ListItem("", "","", "", "", "", "", "", false))
                    rv.setHasFixedSize(true)
                    rv.layoutManager = LinearLayoutManager(requireContext()).apply {
                        orientation = LinearLayoutManager.VERTICAL
                    }
                    rv.adapter = MainViewAdapter(trans_lists)
                }
            } else {
                //トーストで取引がないことを表示
                Toast.makeText(requireContext(), "本日の取引はありません", Toast.LENGTH_LONG).show()
                trans_lists.add(ListItem("", "","", "", "", "", "", "", false))
                rv.setHasFixedSize(true)
                rv.layoutManager = LinearLayoutManager(requireContext()).apply {
                    orientation = LinearLayoutManager.VERTICAL
                }
                rv.adapter = MainViewAdapter(trans_lists)
            }

            return root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.download.setOnClickListener {
            //tran_listをダウンロードする処理
            if (trans_lists[0].flg != false) {
                val csvContent = buildString {
                    append("txid,amount, fee, send_or_receive, partner_address,confidence_type,update_time,transaction\n")
                    trans_lists.forEach { obj ->
                        append("${obj.txid}, ${obj.amount},${obj.fee}, ${obj.sendOrReceive}, ${obj.sendOrReceiveAddress.replace(",", "+")}, ${obj.confidenceType}, ${obj.updateTime}, ${(obj.transaction.replace(",", "+").replace("\n", " "))}\n")
                    }
                }

                startActivity (
                    Intent(requireContext(), DownloadActivity::class.java).apply {
                        putExtra("csv", csvContent)
                    }
                )

            }else{
                Toast.makeText(requireContext(), "トランザクションがありません", Toast.LENGTH_LONG).show()
            }

        }
    }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

}
