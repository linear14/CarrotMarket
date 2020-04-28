package com.dongldh.carrotmarket.write

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.dongldh.carrotmarket.R
import com.dongldh.carrotmarket.dialog.WriteUsedCategoryDialog
import com.dongldh.carrotmarket.setting.SettingLocationActivity
import kotlinx.android.synthetic.main.activity_sign.*
import kotlinx.android.synthetic.main.activity_write_used.*

// SettingLocationActivity에서 변경된 locationNear의 값을 받아온 뒤 텍스트 값을 바꿔 준다.
const val FROM_SETTING_LOCATION = 1000

class WriteUsedActivity : AppCompatActivity(), View.OnClickListener {
    var location: String? = null
    var locationNear: String? = null

    var priceSuggest = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_used)

        // 아래에 있는 지역과 인접정보를 변경해준다.
        location = intent.getStringExtra("location")
        locationNear = intent.getStringExtra("locationNear")
        write_used_near_location_text.text = getString(R.string.write_used_near_location_text)
            .replace("xx", location!!)
            .replace("yy", locationNear!!)

        write_used_category_layout.setOnClickListener(this)
        write_used_price_suggest_check_layout.setOnClickListener(this)
        write_used_near_location_layout.setOnClickListener(this)
        back_image.setOnClickListener(this)

        // 금액 입력 하면 WON 표시에 불 들어오게끔 ^^
        write_used_price_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.isEmpty()) write_used_price_text.setTextColor(Color.parseColor("#cfcfcf"))
                else write_used_price_text.setTextColor(Color.BLACK)
            }
        })
    }

    override fun onClick(v: View?) {
        when(v) {
            // category 설정 가능
            // Dialog 생성자로 textView를 집어 넣었기 때문에, 해당 클래스 안에서 text의 조작이 가능하게끔 설정
            write_used_category_layout -> {
                val dialog = WriteUsedCategoryDialog(write_used_category_text)
                dialog.show(supportFragmentManager, "dialog_event")
            }

            // check를 누를 때 마다 이미지 변경 및 글자 색 변경
            // 그리고, data도 글과 관련된 정보 데이터도 바꿔 줘야 되는거 알지?
            write_used_price_suggest_check_layout -> {
                if(priceSuggest) {
                    priceSuggest = false
                    write_used_price_suggest_check_image.setImageResource(R.drawable.ic_unchecked)
                    write_used_price_suggest_check.setTextColor(Color.parseColor("#B6B6B6"))
                } else {
                    priceSuggest = true
                    write_used_price_suggest_check_image.setImageResource(R.drawable.ic_checked)
                    write_used_price_suggest_check.setTextColor(Color.BLACK)
                }
            }

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
