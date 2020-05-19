package com.dongldh.carrotmarket.setting

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dongldh.carrotmarket.*
import com.dongldh.carrotmarket.database.FROM_SETTING_LOCATION_TO_SELECT_LOCATION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_setting_location.*

class SettingLocationActivity : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    val fireStore = FirebaseFirestore.getInstance()
    lateinit var locationList: ArrayList<String>
    lateinit var locationNearList: ArrayList<Int>

    // 현재 선택되어있는 인덱스.. 이 인덱스를 조작한 뒤, 최종적으로 backButton 을 누를 때 sharedPreference 로 등록하자
    var nowSelected = App.preference.nowSelected

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_location)

        val previousScreen = intent.getStringExtra("requestCode")
        if(previousScreen == "ChangeLocationDialog") {
            title_text.text = getString(R.string.location_setting_toolbar)
            location_setting_layout.visibility = View.VISIBLE

            // 최초 한 번 받아오자
            locationList = intent.getStringArrayListExtra("location")!!
            locationNearList = intent.getIntegerArrayListExtra("locationNear")!!

            init()

            // seekBar의 값에 따라 이미지 + 텍스트 등의 값을 변화시킨다.
            seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    locationNearList[nowSelected] = progress
                    when(progress) {
                        0 -> location_setting_image.setImageResource(R.drawable.location_0)
                        1 -> location_setting_image.setImageResource(R.drawable.location_1)
                        2 -> location_setting_image.setImageResource(R.drawable.location_2)
                        3 -> location_setting_image.setImageResource(R.drawable.location_3)
                    }
                    setNearLocationText(locationList[nowSelected], locationNearList[nowSelected])
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            // 리사이클러뷰로 구현한게 아니라서, 각 버튼마다 이렇게 처리를 해줘야함..
            // 뭔가 하드코딩 느낌이 나는데.. 데이터 이동에 관련된 공부를 어떻게 해야할 지..
            location_select_button1.setOnClickListener {
                nowSelected = 0
                makeSelectedBackground()
                setNearLocationText(locationList[nowSelected], locationNearList[nowSelected])
                seekBarStatus(locationNearList[nowSelected])
            }

            location_select_button2.setOnClickListener {
                if(locationList.size == 1) {
                    val intent = Intent(this, SelectLocationActivity::class.java)
                    intent.putExtra("firstLocation", locationList[0])
                    intent.putExtra("requestCode", "SettingLocationActivity")
                    startActivityForResult(intent, FROM_SETTING_LOCATION_TO_SELECT_LOCATION)
                } else {
                    nowSelected = 1
                    makeSelectedBackground()
                    setNearLocationText(locationList[nowSelected], locationNearList[nowSelected])
                    seekBarStatus(locationNearList[nowSelected])
                }
            }

            location_delete_image1.setOnClickListener {
                makeDeleteDialog(0)
            }

            location_delete_image2.setOnClickListener {
                makeDeleteDialog(1)
            }

            back_image.setOnClickListener {
                // progressBar 생성
                val progressDialog = CircleProgressDialog(this)

                App.preference.nowSelected = nowSelected
                App.preference.location = locationList[nowSelected]
                val uid = auth.currentUser!!.uid
                fireStore.collection("users").document(uid).update("locationNear", locationNearList).addOnCompleteListener {
                    fireStore.collection("users").document(uid).update("location", locationList).addOnCompleteListener {
                        val intent = Intent()
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                        progressDialog.dismiss()    // progressBar 종료
                    }
                }.addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
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
                val progressDialog = CircleProgressDialog(this)
                val uid = auth.currentUser!!.uid
                fireStore.collection("users").document(uid).get().addOnSuccessListener {
                    val locationNearList = it["locationNear"] as ArrayList<Long?>
                    locationNearList[nowSelected] = locationNear.toLong()
                    fireStore.collection("users").document(uid).update("locationNear", locationNearList)

                    val intent = Intent()
                    intent.putExtra("locationNear", locationNear.toString())
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                    progressDialog.dismiss()
                }.addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == FROM_SETTING_LOCATION_TO_SELECT_LOCATION) {
            if(resultCode == Activity.RESULT_OK) {
                locationList.add(data?.getStringExtra("newLocation")!!)
                locationNearList.add(1)
                init()
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
        if(nowSelected == 0) {
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

    // 다이얼로그 만들기
    fun makeDeleteDialog(position: Int) {
        // 만약 locationList의 사이즈가 1이면 -> 동네 설정 액티비티로 이동할 수 있도록 유도
        if(locationList.size == 1) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("동네는 최소 1개이상 선택되어 있어야 합니다. 현재 설정된 동네를 변경하시겠어요?")

            builder.setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            builder.setPositiveButton("네, 변경할게요") { dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        } else {    // locationList의 사이즈가 2라면? -> 삭제할건지 말건지 정하는 다이얼로그 띄워주기
            val builder = AlertDialog.Builder(this)
            builder.setMessage("선택한 지역을 삭제하시겠습니까?")

            builder.setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }

            // 삭제 하겠냐고 물어봤을 때 확인 버튼을 누른다? -> 데이터 처리 및 UI변경
            builder.setPositiveButton("확인") { dialog, which ->
                if(position == 0) {
                    val tempLocation = locationList[1]
                    val tempLocationNear = locationNearList[1]

                    locationList.removeAt(position)
                    locationNearList.removeAt(position)

                    locationList[0] = tempLocation
                    locationNearList[0] = tempLocationNear
                } else {
                    locationList.removeAt(position)
                    locationNearList.removeAt(position)
                }
                nowSelected = 0
                init()
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    // 버튼 텍스트, 이미지, 배경등 초기 설정
    private fun init() {
        makeSelectedBackground()
        
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

        setNearLocationText(locationList[nowSelected], locationNearList[nowSelected])
        seekBarStatus(locationNearList[nowSelected])
    }
}
