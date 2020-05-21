package com.dongldh.carrotmarket

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.dongldh.carrotmarket.database.DataUser
import com.dongldh.carrotmarket.database.FROM_CHANGE_LOCATION_TO_SETTING_LOCATION
import com.dongldh.carrotmarket.database.FROM_MAIN_TO_SETTING_CATEGORY
import com.dongldh.carrotmarket.dialog.ChangeLocationDialog
import com.dongldh.carrotmarket.dialog.SuggestLoginDialog
import com.dongldh.carrotmarket.dialog.WriteBottomSheetDialog
import com.dongldh.carrotmarket.main_fragment.CategoryFragment
import com.dongldh.carrotmarket.main_fragment.ChatFragment
import com.dongldh.carrotmarket.main_fragment.HomeFragment
import com.dongldh.carrotmarket.main_fragment.MyCarrotFragment
import com.dongldh.carrotmarket.setting.SettingCategoryActivity
import com.google.android.gms.maps.model.Circle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

// fragment가 중첩되어 있는 상태일 경우 true 부여. true인 상태에서는 뒤로가는 버튼 누를 경우 이전 프래그먼트로 돌아가야함
var nestedFragmentState = false
class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    var auth: FirebaseAuth? = FirebaseAuth.getInstance()
    val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var isLoading = true // 처음 시작할 때 데이터를 받아오는 중인지를 확인 (데이터를 받아오는 중이라면, UI가 이상해지지 않도록 초기 설정을 몇몇 해줘야 함)

    val user = DataUser()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("LifeCycle", "MainActivity_onCreate()")

        getUserInfo()
        selected_location_layout.setOnClickListener(this)
        location_setting_image.setOnClickListener(this)
        bottom_navigation.setOnNavigationItemSelectedListener(this)


        // 만약 세션 유지 된 계정이 존재하지 않는다면 Dialog를 띄워서 로그인 / 회원가입을 권유한다.
        if(auth?.currentUser == null) showSuggestLoginDialog()
    }


    override fun onClick(v: View?) {
        when(v) {
            selected_location_layout -> {
                val changeLocationDialog = ChangeLocationDialog()
                val bundle = Bundle()
                bundle.putString("nowLocation", title_text.text.toString()) // 현재 설정되어 있는 Location의 글자를 bold처리 하기 위해 가져감
                bundle.putStringArrayList("location", user.location)
                bundle.putIntegerArrayList("locationNear", user.locationNear)
                changeLocationDialog.arguments = bundle
                changeLocationDialog.show(supportFragmentManager, "dialog_fragment")
            }

            location_setting_image -> {
                val intent = Intent(this@MainActivity, SettingCategoryActivity::class.java)
                startActivityForResult(intent, FROM_MAIN_TO_SETTING_CATEGORY)
            }
        }
    }

    // BottomNavigation 선택시 화면 전환
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                selected_location_layout.isClickable = true
                nestedFragmentState = false
                val homeFragment = HomeFragment()
                
                // 아래의 bundle 계속 공통으로 들어가는데, 이것도 한 곳에다가 묶어야겠음
                val bundle = Bundle()
                bundle.putString("location", user.location[App.preference.nowSelected])
                bundle.putString("locationNear", user.locationNear[App.preference.nowSelected].toString())
                homeFragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.main_content, homeFragment).commit()

                // 프래그먼트간 이동에서 title_text의 값을 설정 할 필요가 있는 경우 다음과 같이 진행.
                if(auth?.currentUser == null) {
                    title_text.text = App.preference.location
                } else {
                    if(!isLoading) title_text.text = user.location[App.preference.nowSelected]
                }
                selected_location_image.visibility = View.VISIBLE
                search_image.visibility = View.VISIBLE
                location_setting_image.visibility = View.VISIBLE
                alarm_image.visibility = View.VISIBLE
                setting_image.visibility= View.GONE

                return true
            }

            R.id.action_category -> {
                val categoryFragment = CategoryFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, categoryFragment).commit()

                title_text.text = "카테고리"
                selected_location_layout.isClickable = false
                selected_location_image.visibility = View.GONE
                search_image.visibility = View.VISIBLE
                location_setting_image.visibility = View.GONE
                alarm_image.visibility = View.VISIBLE
                setting_image.visibility= View.GONE

                return true
            }

            R.id.action_write -> {
                if(auth?.currentUser == null) showSuggestLoginDialog()
                else {
                    val dialog = WriteBottomSheetDialog()
                    val bundle = Bundle()
                    bundle.putString("location", user.location[App.preference.nowSelected])
                    bundle.putString("locationNear", user.locationNear[App.preference.nowSelected].toString())
                    dialog.arguments = bundle
                    dialog.show(supportFragmentManager, "dialog_bottom")
                }
                return false
            }

            R.id.action_chat -> {
                if(auth?.currentUser == null) {
                    showSuggestLoginDialog()
                    return false
                } else {
                    val chatFragment = ChatFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_content, chatFragment).commit()

                    title_text.text = "채팅"
                    selected_location_layout.isClickable = false
                    selected_location_image.visibility = View.GONE
                    search_image.visibility = View.GONE
                    location_setting_image.visibility = View.GONE
                    alarm_image.visibility = View.GONE
                    setting_image.visibility = View.GONE

                    return true
                }
            }

            R.id.action_my_carrot -> {
                val bundle = Bundle()
                bundle.putString("phone", user.phone)
                bundle.putString("userName", user.userName)
                bundle.putString("location", user.location[App.preference.nowSelected])
                bundle.putString("profileImage", user.profileImage)

                val myCarrotFragment = MyCarrotFragment()
                myCarrotFragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.main_content, myCarrotFragment).commit()

                title_text.text = "나의 당근"
                selected_location_layout.isClickable = false
                selected_location_image.visibility = View.GONE
                search_image.visibility = View.GONE
                location_setting_image.visibility = View.GONE
                alarm_image.visibility = View.GONE
                setting_image.visibility= View.VISIBLE

                return true
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            FROM_CHANGE_LOCATION_TO_SETTING_LOCATION -> {
                if(resultCode == Activity.RESULT_OK) {
                    recreate()
                }
            }

            FROM_MAIN_TO_SETTING_CATEGORY -> {
                if(resultCode == Activity.RESULT_OK) {
                    recreate()
                }
            }
        }
    }

    // 뒤로가기 버튼 눌렀을 경우
    var currentTime = System.currentTimeMillis()
    override fun onBackPressed() {
        // true일 경우 이전 프래그먼트로 돌아오기, false일 경우 메인 화면이므로 버튼 두 번 눌러 앱 종료 구현
        if(nestedFragmentState) {
            nestedFragmentState = false
            currentTime = 0
            findViewById<View>(R.id.detail_content)?.visibility = View.GONE
            findViewById<View>(R.id.bottom_navigation)?.visibility = View.VISIBLE
        } else {
            if (System.currentTimeMillis() - currentTime > 2000) {
                currentTime = System.currentTimeMillis()
                Toast.makeText(this, "'뒤로'버튼 한 번 더 누르면 종료", Toast.LENGTH_SHORT).show()
            } else {
                super.onBackPressed()
            }
        }
    }

    // 유저의 기본 정보 받아오기. 필요한 프래그먼트로 binding 시켜서 보내줄 데이터 (나중에 bundle로 넣어주면 될걸)
    // 각 프래그먼트에서 진행하면 텍스트 뷰에 값이 들어가는 반응이 너무 느리더라.. 그래서 MainActivity에서 진행을 해보기로 결정
    // 기본적으로 activity가 실행되면 유저의 정보를 가장 먼저 받아오도록 설정했기 때문에, MainActivity에서의 유저 정보는 user인스턴스에서 가져다 사용하면 됩니다.
    private fun getUserInfo() {
        val uid = auth?.currentUser?.uid
        if(uid != null) {
            val progressDialog = CircleProgressDialog(this)
            fireStore.collection("users").document(uid).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                // 데이터가 변경 될 때마다 SnapshotListener가 동작하므로, 오류 발생할 경우를 return으로 잡아줘야함
                // 로그아웃 시 여기서 오류가 발생해서 우선은 이렇게 처리를 했는데, 또 다른 경우에서 오류가 발생한다면?
                // val item 쪽에서 null값을 받을 경우 default값으로 줬던 ?: DataUser() 부분을 조작해보자.
                if(firebaseFirestoreException != null) {
                    Log.d("Snapshot_Exception", "Listen failed. $firebaseFirestoreException")
                    return@addSnapshotListener
                }

                // 메소드 콜백이 되므로, 어디에선가 데이터를 변경할 경우 자동으로 user의 정보를 바꿔준다. 아주 편리함.
                Log.d("MethodCallStack", "MainActivity_getUserInfo() call...")
                val item = documentSnapshot?.toObject(DataUser::class.java) // ?: DataUser()
                user.phone = item!!.phone
                user.userName = item.userName
                user.location = item.location
                user.profileImage = item.profileImage
                user.locationNear = item.locationNear

                // 기본적으로 title_text는 회원의 지역정보를 반영한다.
                title_text.text = user.location[App.preference.nowSelected]

                // 로딩중이라면 (즉, 처음 메인 화면에 들어오면), Fragment의 화면 설정을 여기서 해줘야 오류 안생김
                // 만약, loading중인지 확인 안하고, 무턱대고 fragment를 action_home으로 바꾸면 예외가 발생하는데,
                // 다른페이지에서 지역정보 설정을 바꾸면 이 콜백 메서드가 작동하기 때문이다.
                // https://devvkkid.tistory.com/87 에서 힌트를 얻었음
                if(isLoading) bottom_navigation.selectedItemId = R.id.action_home
                isLoading = false
                progressDialog.dismiss()
            }
        }
    }

    // 로그인이 되어있지 않은 상태에서 로그인 권한을 요구하는 프래그먼트를 혹은 동작을 실행했을 때 띄워지는 Dialog
    // location 값을 보내주어 값을 넣어줄 수 있도록
    private fun showSuggestLoginDialog() {
        val dialog = SuggestLoginDialog()
        dialog.show(supportFragmentManager, "dialog_event")
    }
}
