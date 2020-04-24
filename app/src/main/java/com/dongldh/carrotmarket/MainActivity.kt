package com.dongldh.carrotmarket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.dongldh.carrotmarket.database.DataUser
import com.dongldh.carrotmarket.dialog.SuggestLoginDialog
import com.dongldh.carrotmarket.dialog.WriteBottomSheetDialog
import com.dongldh.carrotmarket.main_fragment.CategoryFragment
import com.dongldh.carrotmarket.main_fragment.ChatFragment
import com.dongldh.carrotmarket.main_fragment.HomeFragment
import com.dongldh.carrotmarket.main_fragment.MyCarrotFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_my_carrot.view.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    var auth: FirebaseAuth? = FirebaseAuth.getInstance()
    val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    val user = DataUser()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getUserInfo()

        bottom_navigation.setOnNavigationItemSelectedListener(this)
        bottom_navigation.selectedItemId = R.id.action_home

        // 만약 세션 유지 된 계정이 존재하지 않는다면 Dialog를 띄워서 로그인 / 회원가입을 권유한다.
        if(auth?.currentUser == null) {
            val dialog = SuggestLoginDialog()
            dialog.show(supportFragmentManager, "dialog_event")
        }
    }

    // BottomNavigation 선택시 화면 전환
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                val homeFragment = HomeFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, homeFragment)
                    .commit()

                title_text.text = App.preference.location
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
                selected_location_image.visibility = View.GONE
                search_image.visibility = View.VISIBLE
                location_setting_image.visibility = View.GONE
                alarm_image.visibility = View.VISIBLE
                setting_image.visibility= View.GONE

                return true
            }

            R.id.action_write -> {
                if(auth?.currentUser == null) {
                    val dialog = SuggestLoginDialog()
                    dialog.show(supportFragmentManager, "dialog_event")
                } else {
                    val dialog = WriteBottomSheetDialog()
                    dialog.show(supportFragmentManager, "dialog_bottom")
                }
                return false
            }

            R.id.action_chat -> {
                if(auth?.currentUser == null) {
                    val dialog = SuggestLoginDialog()
                    dialog.show(supportFragmentManager, "dialog_event")

                    return false
                } else {
                    val chatFragment = ChatFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_content, chatFragment).commit()

                    title_text.text = "채팅"
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
                bundle.putString("location", user.location)
                bundle.putString("profileImage", user.profileImage)

                val myCarrotFragment = MyCarrotFragment()
                myCarrotFragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.main_content, myCarrotFragment).commit()

                title_text.text = "나의 당근"
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

    // 뒤로가기 버튼 눌렀을 경우
    var currentTime = System.currentTimeMillis()
    override fun onBackPressed() {
        if(System.currentTimeMillis() - currentTime > 2000) {
            currentTime = System.currentTimeMillis()
            Toast.makeText(this, "'뒤로'버튼 한 번 더 누르면 종료", Toast.LENGTH_SHORT).show()
        } else {
            super.onBackPressed()
        }
    }

    // 유저의 기본 정보 받아오기. 필요한 프래그먼트로 binding 시켜서 보내줄 데이터 (나중에 bundle로 넣어주면 될걸)
    // 각 프래그먼트에서 진행하면 텍스트 뷰에 값이 들어가는 반응이 너무 느리더라.. 그래서 MainActivity에서 진행을 해보기로 결정
    fun getUserInfo() {
        val uid = auth?.currentUser?.uid
        if(uid != null) {
            fireStore.collection("users").document(uid).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                val item = documentSnapshot?.toObject(DataUser::class.java)
                user.phone = item!!.phone
                user.userName = item.userName
                user.location = item.location
                user.profileImage = item.profileImage
            }
        }
    }
}
