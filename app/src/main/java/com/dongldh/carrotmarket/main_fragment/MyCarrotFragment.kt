package com.dongldh.carrotmarket.main_fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dongldh.carrotmarket.R
import com.dongldh.carrotmarket.setting.SettingMainActivity
import kotlinx.android.synthetic.main.fragment_my_carrot.*
import kotlinx.android.synthetic.main.fragment_my_carrot.view.*

class MyCarrotFragment: Fragment(), View.OnClickListener {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_carrot, container, false)

        view.app_setting_layout.setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        when(v) {
            // 앱 설정 액티비티로 이동
            app_setting_layout -> {
                val intent = Intent(activity, SettingMainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}