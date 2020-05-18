package com.dongldh.carrotmarket.setting

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.dongldh.carrotmarket.App
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

        val previousScreen = intent.getStringExtra("requestCode")
        if(previousScreen == "ChangeLocationDialog") {
            title_text.text = getString(R.string.location_setting_toolbar)
            location_setting_layout.visibility = View.VISIBLE

            makeSelectedBackground()
            val locationList = intent.getStringArrayListExtra("location")!!
            val locationNearList = intent.getIntegerArrayListExtra("locationNear")!!

            // 내 동네 정보를 각 뷰에다가 보여지도록 설정함 (locationList의 사이즈에 따라 갯수가 결정됨)
            location_select_button1.text = locationList[0]
            if(locationList.size == 2) {
                location_select_button2.text = locationList[1]
                location_add_image.visibility = View.GONE
                location_delete_image2.visibility = View.VISIBLE
            } else {
                location_select_button2.text = null
                location_add_image.visibility = View.VISIBLE
                location_delete_image2.visibility = View.GONE
            }

            setNearLocationText(locationList[App.preference.nowSelected], locationNearList[App.preference.nowSelected])
            seekBarStatus(locationNearList[App.preference.nowSelected])

            // seekBar의 값에 따라 이미지 + 텍스트 등의 값을 변화시킨다.
            seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    locationNearList[App.preference.nowSelected] = progress
                    when(progress) {
                        0 -> location_setting_image.setImageResource(R.drawable.location_0)
                        1 -> location_setting_image.setImageResource(R.drawable.location_1)
                        2 -> location_setting_image.setImageResource(R.drawable.location_2)
                        3 -> location_setting_image.setImageResource(R.drawable.location_3)
                    }
                    setNearLocationText(locationList[App.preference.nowSelected], locationNearList[App.preference.nowSelected])
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            // 리사이클러뷰로 구현한게 아니라서, 각 버튼마다 이렇게 처리를 해줘야함..
            // 뭔가 하드코딩 느낌이 나는데.. 데이터 이동에 관련된 공부를 어떻게 해야할 지..
            location_select_button1.setOnClickListener {
                App.preference.nowSelected = 0
                makeSelectedBackground()
                setNearLocationText(locationList[App.preference.nowSelected], locationNearList[App.preference.nowSelected])
                seekBarStatus(locationNearList[App.preference.nowSelected])
            }

            location_select_button2.setOnClickListener {
                App.preference.nowSelected = 1
                makeSelectedBackground()
                setNearLocationText(locationList[App.preference.nowSelected], locationNearList[App.preference.nowSelected])
                seekBarStatus(locationNearList[App.preference.nowSelected])
            }

            back_image.setOnClickListener {
                val uid = auth.currentUser!!.uid
                fireStore.collection("users").document(uid).update("locationNear", locationNearList)
                fireStore.collection("users").document(uid).update("location", locationList)

                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

        }


        else if(previousScreen == "WriteUsedActivity") {
            title_text.text = getString(R.string.select_location_area_toolbar)
            location_setting_layout.visibility = View.GONE

            // 초기 값 세팅
            val location = intent.getStringExtra("location")!!
            var locationNear = intent.getStringExtra("locationNear")!!.toInt()
            setNearLocationText(location, locationNear)
            seekBarStatus(locationNear)

            // seekBar의 값에 따라 이미지 + 텍스트 등의 값을 변화시킨다.
            seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    locationNear = progress
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
                fireStore.collection("users").document(uid).get().addOnSuccessListener {
                    val locationNearList = it["locationNear"] as ArrayList<Long?>
                    locationNearList[App.preference.nowSelected] = locationNear.toLong()
                    fireStore.collection("users").document(uid).update("locationNear", locationNearList)
                }

                val intent = Intent()
                intent.putExtra("locationNear", locationNear.toString())
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    // 지역과 근처지역 정보를 나타내는 텍스트를 설정하고 보여주는 메서드
    fun setNearLocationText(location: String, locationNear: Int) {
        near_location_text.text = getString(R.string.near_location_text)
            .replace("xx", location)
            .replace("yy", locationNear.toString())
    }

    // 선택된 지역에 따라 백그라운드를 바꿔주는 메서드
    fun makeSelectedBackground() {
        if(App.preference.nowSelected == 0) {
            location_select_layout1.setBackgroundResource(R.drawable.decorate_button_active)
            location_select_layout2.setBackgroundResource(R.drawable.decorate_button_inactive)
        } else {
            location_select_layout1.setBackgroundResource(R.drawable.decorate_button_inactive)
            location_select_layout2.setBackgroundResource(R.drawable.decorate_button_active)
        }
    }

    // seekBar의 위치에 따라 이미지를 바꿔주는 메서드
    fun seekBarStatus(locationNear: Int) {
        seekBar.progress = locationNear
        when(locationNear) {
            0 -> location_setting_image.setImageResource(R.drawable.location_0)
            1 -> location_setting_image.setImageResource(R.drawable.location_1)
            2 -> location_setting_image.setImageResource(R.drawable.location_2)
            3 -> location_setting_image.setImageResource(R.drawable.location_3)
        }
    }
}
