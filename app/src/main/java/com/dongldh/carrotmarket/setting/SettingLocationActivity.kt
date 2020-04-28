package com.dongldh.carrotmarket.setting

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.dongldh.carrotmarket.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_setting_location.*

class SettingLocationActivity : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    val fireStore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_location)

        // 초기 값 세팅
        val location = intent.getStringExtra("location")!!
        var locationNear = intent.getStringExtra("locationNear")!!.toLong()
        setNearLocationText(location, locationNear)
        seekBar.progress = locationNear.toInt()
        when(locationNear) {
            0L -> location_setting_image.setImageResource(R.drawable.location_0)
            1L -> location_setting_image.setImageResource(R.drawable.location_1)
            2L -> location_setting_image.setImageResource(R.drawable.location_2)
            3L -> location_setting_image.setImageResource(R.drawable.location_3)
        }


        // seekBar의 값에 따라 이미지 + 텍스트 등의 값을 변화시킨다.
        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                locationNear = progress.toLong()
                when(progress) {
                    0 -> location_setting_image.setImageResource(R.drawable.location_0)
                    1 -> location_setting_image.setImageResource(R.drawable.location_1)
                    2 -> location_setting_image.setImageResource(R.drawable.location_2)
                    3 -> location_setting_image.setImageResource(R.drawable.location_3)
                }
                setNearLocationText(location, locationNear)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })


        // 다시 locationNear의 값을 돌려주자~ WriteUsedActivity에서 반영될 수 있도록^^
        // 여기서 firestore의 데이터를 바꾸주면.. MainActivity에서의 getUserInfo안의 callback이 작동하므로
        // 특별한 조작을 하지 않아도 MainActivity에서의 DataUser의 정보가 바뀌는 아주 편리한 상황이 생긴다.
        back_image.setOnClickListener {
            val uid = auth.currentUser!!.uid
            fireStore.collection("users").document(uid).update("locationNear", locationNear)

            val intent = Intent()
            intent.putExtra("locationNear", locationNear.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    fun setNearLocationText(location: String, locationNear: Long) {
        near_location_text.text = getString(R.string.near_location_text)
            .replace("xx", location)
            .replace("yy", locationNear.toString())
    }
}
