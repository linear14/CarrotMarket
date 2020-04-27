package com.dongldh.carrotmarket.write

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dongldh.carrotmarket.R
import com.dongldh.carrotmarket.setting.SettingLocationActivity
import kotlinx.android.synthetic.main.activity_write_used.*

// SettingLocationActivity에서 변경된 locationNear의 값을 받아온 뒤 텍스트 값을 바꿔 준다.
const val FROM_SETTING_LOCATION = 1000

class WriteUsedActivity : AppCompatActivity(), View.OnClickListener {
    var location: String? = null
    var locationNear: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_used)

        // 아래에 있는 지역과 인접정보를 변경해준다.
        location = intent.getStringExtra("location")
        locationNear = intent.getStringExtra("locationNear")
        write_used_near_location_text.text = getString(R.string.write_used_near_location_text)
            .replace("xx", location!!)
            .replace("yy", locationNear!!)


        write_used_near_location_layout.setOnClickListener(this)
        back_image.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            write_used_near_location_layout -> {
                // 다음 액티비티에서 firebase로 정보를 받아올 수 있음에도 불구하고
                // 계속 이렇게 intent로 값을 넘겨주는 이유는? -> 딜레이 현상 없이 바로바로 뷰에 값이 저장되도록 하기 위해서
                val intent = Intent(this, SettingLocationActivity::class.java)
                intent.putExtra("location", location)
                intent.putExtra("locationNear", locationNear)
                startActivityForResult(intent, FROM_SETTING_LOCATION)
            }

            back_image -> {
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            FROM_SETTING_LOCATION -> {
                if(resultCode == Activity.RESULT_OK) {
                    locationNear = data?.getStringExtra("locationNear")
                    write_used_near_location_text.text = getString(R.string.write_used_near_location_text)
                        .replace("xx", location!!)
                        .replace("yy", locationNear!!)
                }
            }
        }
    }
}
