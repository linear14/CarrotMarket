package com.dongldh.carrotmarket

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dongldh.carrotmarket.database.DataUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile.*
import java.lang.Exception
import java.lang.IllegalStateException

class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore

    // 인텐트로부터 받은 값
    var phoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()
        next_text.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            next_text -> {
                phoneNumber = intent.getStringExtra("phoneNumber")

                val email = "${phoneNumber!!}@test.com"
                val password = "123456"

                // 신규 계정 생성
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    // 회원가입 성공
                    if (task.isSuccessful) {
                        // 회원 정보를 firestore database에 저장
                        val uid = task.result?.user?.uid ?: throw IllegalStateException("uid 에서 null값 존재")
                        val phone = phoneNumber!!
                        val userName = nickname_input.text.toString()
                        val location = App.preference.location ?: "이건 null 일 수가 없어 - 오류발생~~!"
                        val profileImage = "image"
                        val dataUser = DataUser(phone, userName, location, profileImage)

                        // Log.d("profile", "uid: $uid, phone: $phone")
                        fireStore.collection("users").document(uid).set(dataUser)

                        Toast.makeText(this, "회원가입 완료!", Toast.LENGTH_SHORT).show()

                        // 회원가입 된 아이디로 로그인
                        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
                            if (it.isSuccessful) {
                                val intent = Intent(this@ProfileActivity, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()
                            }
                        }
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
