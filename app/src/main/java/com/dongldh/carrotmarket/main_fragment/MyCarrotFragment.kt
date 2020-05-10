package com.dongldh.carrotmarket.main_fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.dongldh.carrotmarket.App
import com.dongldh.carrotmarket.R
import com.dongldh.carrotmarket.database.DataUser
import com.dongldh.carrotmarket.setting.SettingMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_my_carrot.*
import kotlinx.android.synthetic.main.fragment_my_carrot.view.*

class MyCarrotFragment: Fragment(), View.OnClickListener {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_carrot, container, false)

        view.profile_name.text = arguments?.getString("userName")
        view.profile_location.text = arguments?.getString("location")
        // Log.d("ArgumentTest", arguments?.getString("profileImage"))
        if(arguments?.getString("profileImage") != "default") {
            Glide.with(activity?.applicationContext!!)
                .load(Uri.parse(arguments?.getString("profileImage")))
                .into(view.profile_image)
        }
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