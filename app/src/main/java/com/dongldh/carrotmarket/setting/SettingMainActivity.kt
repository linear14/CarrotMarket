package com.dongldh.carrotmarket.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.dongldh.carrotmarket.App
import com.dongldh.carrotmarket.R
import com.dongldh.carrotmarket.StartActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_setting_main.*

class SettingMainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_main)

        back_image.setOnClickListener(this)
        sign_out_text.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            back_image -> {
                finish()
            }

            // 로그아웃 관련된 정보
            sign_out_text -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.logout_dialog_title)
                builder.setMessage(R.string.logout_dialog_message)
                builder.setNegativeButton(R.string.logout_dialog_negative) { dialog, which ->
                    dialog.dismiss()
                }
                builder.setPositiveButton(R.string.logout_dialog_positive) { dialog, which ->
                    FirebaseAuth.getInstance().signOut()
                    dialog.dismiss()
                    App.preference.location = null

                    val intent = Intent(this, StartActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                val dialog = builder.create()
                dialog.show()
            }
        }
    }
}
