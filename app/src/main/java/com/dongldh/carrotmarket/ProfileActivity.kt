package com.dongldh.carrotmarket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth

    // 인텐트로부터 받은 값
    var phoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        next_text.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            next_text -> {
                auth = FirebaseAuth.getInstance()
                phoneNumber = intent.getStringExtra("phoneNumber")

                val email = "${phoneNumber!!}@test.com"
                val password = "123456"

                // 신규 계정 생성
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 회원가입 성공
                        Toast.makeText(this, "회원가입 완료!", Toast.LENGTH_SHORT).show()

                        // 회원가입 된 아이디로 로그인
                        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val intent = Intent(this@ProfileActivity, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }

                        // ---------- user 개인정보에 사진과 닉네임, 지역 설정 하는 것 구현해야 함 ----------
                        val todo = "로그 찍어둘테니 일해라 동현아"

                    } else {
                        // 회원가입 실패
                        Log.d("FirebaseAuth", "onComplete" + task.exception!!.message)
                        Toast.makeText(this, "회원가입 실패...", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
