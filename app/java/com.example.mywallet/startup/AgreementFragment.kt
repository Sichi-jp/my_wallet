package com.example.mywallet.startup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mywallet.MainActivity
import com.example.mywallet.R

class AgreementFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.agreement, container, false)
        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)

        view.findViewById<TextView>(R.id.button).setOnClickListener {
            if(checkBox.isChecked){
                startActivity(Intent(context, MainActivity::class.java))
                activity?.finish()
            }
        }
        return view
    }

}
