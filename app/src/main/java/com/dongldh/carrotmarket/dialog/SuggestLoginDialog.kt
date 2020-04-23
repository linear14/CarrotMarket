package com.dongldh.carrotmarket.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.dongldh.carrotmarket.R
import com.dongldh.carrotmarket.SignActivity
import kotlinx.android.synthetic.main.dialog_suggest_login.view.*

class SuggestLoginDialog: DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_suggest_login, container, false)
        isCancelable = false

        view.no_login_text.setOnClickListener {
            dismiss()
        }

        view.login_button.setOnClickListener {
            val intent = Intent(activity, SignActivity::class.java)
            startActivity(intent)
            dismiss()
        }

        return view
    }
}