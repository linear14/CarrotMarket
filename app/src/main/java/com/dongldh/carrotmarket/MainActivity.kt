package com.dongldh.carrotmarket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.dongldh.carrotmarket.dialog.SuggestLoginDialog
import com.dongldh.carrotmarket.dialog.WriteBottomSheetDialog
import com.dongldh.carrotmarket.main_fragment.CategoryFragment
import com.dongldh.carrotmarket.main_fragment.ChatFragment
import com.dongldh.carrotmarket.main_fragment.HomeFragment
import com.dongldh.carrotmarket.main_fragment.MyCarrotFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation.setOnNavigationItemSelectedListener(this)
        bottom_navigation.selectedItemId = R.id.action_home

        auth = FirebaseAuth.getInstance()
        if(auth!!.currentUser == null) {
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
                val dialog = WriteBottomSheetDialog()
                dialog.show(supportFragmentManager, "dialog_bottom")

                return false
            }

            R.id.action_chat -> {
                val chatFragment = ChatFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, chatFragment).commit()

                title_text.text = "채팅"
                selected_location_image.visibility = View.GONE
                search_image.visibility = View.GONE
                location_setting_image.visibility = View.GONE
                alarm_image.visibility = View.GONE
                setting_image.visibility= View.GONE

                return true
            }

            R.id.action_my_carrot -> {
                val myCarrotFragment = MyCarrotFragment()
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
}
