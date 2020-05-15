package com.dongldh.carrotmarket.dialog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.dongldh.carrotmarket.App
import com.dongldh.carrotmarket.R
import com.dongldh.carrotmarket.SignActivity
import kotlinx.android.synthetic.main.dialog_suggest_login.*
import kotlinx.android.synthetic.main.dialog_suggest_login.view.*

class SuggestLoginDialog: DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        val view = inflater.inflate(R.layout.dialog_suggest_login, container, false)
        isCancelable = false

        view.suggest_login_text.text = getFullLoginText()

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

    // 로그인 되어있지 않을 경우, SharedPreference에 저장해 둔 location을 메시지에 포함해서 띄워줌
    private fun getFullLoginText(): String = getString(R.string.suggest_login_text)
        .replace("xx", App.preference.location ?: "xx")

}