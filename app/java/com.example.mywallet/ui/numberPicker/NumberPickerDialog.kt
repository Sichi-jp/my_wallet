package com.example.mywallet.ui.numberPicker

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.example.mywallet.R
import java.time.LocalDateTime
import kotlin.properties.Delegates

class NumberPickerDialog():DialogFragment(){

    //Dialogのレイアウト定義
    private lateinit var dialogView: View
    // OKキャンセルボタンが押された時のリスナー定義
    private lateinit var listener: NoticeDialogListener
    // 選択された年の定義
    private var selectedYearItem by Delegates.notNull<Int>()
    // 選択された月の定義
    private var selectedMonthItem by Delegates.notNull<Int>()

    //OK、キャンセルボタンがが押された時用のインターフェース
    interface NoticeDialogListener {
        fun onNumberPickerDialogPositiveClick(dialog: DialogFragment, selectedYearItem: Int, selectedMonthItem:Int)
        fun onNumberPickerDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            val fragment = parentFragment
            //親フラグメントがNoticeDialogListenerを継承していることを明示している
            this.listener = fragment as NoticeDialogListener
        }catch (e: ClassCastException){
            throw  ClassCastException("$context must implement NoticeDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //レイアウトの指定
        dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_numberpicker,null)
        val builder = AlertDialog.Builder(requireContext())
        //現在日付の取得
        val current = LocalDateTime.now()

        builder.setView(dialogView)
        builder.setTitle("年月の入力")
        builder.setMessage("表示したい取引の年月をご入力ください")

        //OKボタンが押された時の処理
        builder.setPositiveButton("OK"){_,_ -> this.listener.onNumberPickerDialogPositiveClick(this,selectedYearItem,selectedMonthItem)}
        //キャンセルボタンが押された時の処理
        builder.setNegativeButton("キャンセル"){_,_ -> this.listener.onNumberPickerDialogNegativeClick(this) }


        /**
         * 年のNumberpickerの初期化
         * デフォルト値：現在年
         * 最小値：現在年 - 1
         * 最大値：現在年 + 30
         */
        val yearNumberPicker = dialogView.findViewById<NumberPicker>(R.id.yearNumberPicker)
        //現在値の変更があった時に、現在フォーカスの当たっている値を取得し、selectedYearItemに格納
        yearNumberPicker.setOnValueChangedListener(object :NumberPicker.OnValueChangeListener{
            override fun onValueChange(picker: NumberPicker?, old: Int, new: Int) {
                selectedYearItem = new
            }
        })
        //最小値の設定
        yearNumberPicker.minValue = current.year-1
        //最大値の設定
        yearNumberPicker.maxValue = current.year+30
        //yearNumberPickerの初期値に今年を入れた
        yearNumberPicker.value = current.year
        //selectedYearItemにも同じく今年を入れておく
        selectedYearItem = yearNumberPicker.value

        /**
         * 月のNumberpickerの初期化
         * デフォルト値：今月
         * 最小値：1
         * 最大値：12
         */
        val monthNumberPicker = dialogView.findViewById<NumberPicker>(R.id.monthNumberPicker)
        monthNumberPicker.setOnValueChangedListener(object :NumberPicker.OnValueChangeListener{
            override fun onValueChange(picker: NumberPicker?, old: Int, new: Int) {
                selectedMonthItem = new
            }
        })
        monthNumberPicker.minValue = 1
        monthNumberPicker.maxValue = 12
        monthNumberPicker.value = current.monthValue
        selectedMonthItem = monthNumberPicker.value

        return builder.create()
    }
}
