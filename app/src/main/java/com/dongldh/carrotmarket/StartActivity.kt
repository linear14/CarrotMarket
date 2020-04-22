package com.dongldh.carrotmarket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide

import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 위치 정보가 저장이 되어 있지 않으면 startActivity, 저장이 되어 있다면 mainActivity로 이동
        if(App.preference.location != null) {
            val intent = Intent(this@StartActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            setContentView(R.layout.activity_start)

            // ImageView 안의 gif가 움직일 수 있도록 설정하자 (Glide 라이브러리 이용)
            Glide.with(this).load(R.drawable.rabbit).centerCrop().into(rabbit_image)

            select_location_button.setOnClickListener {
                val intent = Intent(this@StartActivity, SelectLocationActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
