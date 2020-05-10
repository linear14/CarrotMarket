package com.dongldh.carrotmarket

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dongldh.carrotmarket.database.DataUser
import com.dongldh.carrotmarket.database.PICK_IMAGE_FROM_ALBUM
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile.*
import java.lang.Exception
import java.lang.IllegalStateException

class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore
    var storage: FirebaseStorage? = null

    // 인텐트로부터 받은 값
    var phoneNumber: String? = null
    var profileImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        profile_image.setOnClickListener(this)
        next_text.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            // 프로필 이미지 선택
            profile_image -> {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_FROM_ALBUM)
            }

            next_text -> {
                phoneNumber = intent.getStringExtra("phoneNumber")

                val email = "${phoneNumber!!}@test.com"
                val password = "123456"

                // 신규 계정 생성
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    // 회원가입 성공
                    if (task.isSuccessful) {
                        val uid = task.result?.user?.uid ?: throw IllegalStateException("uid 에서 null값 존재")
                        // 프로필 사진을 storage에 저장
                        val storageReference = storage?.reference
                        val imageFileName = "USER_IMAGE_${uid}.png"

                        if(profileImage != null) {
                            storageReference?.child("userImages")?.child(imageFileName)?.putFile(Uri.parse(profileImage))?.addOnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    storageReference.child("userImages").child(imageFileName).downloadUrl.addOnCompleteListener { task3 ->
                                        if (task3.isSuccessful) {
                                            profileImage = task3.result.toString()
                                            saveUserInfoToFIreStoreAndLogin(uid, email, password)
                                        } else {
                                            // 만약 이미지는 저장이 됐지만 서버 오류등으로 이미지 url을 가져오지 못했다면, 이미지 삭제 및 토스트 메세지 띄움
                                            storageReference.child("userImages").child(imageFileName).delete()
                                            Toast.makeText(this, "프로필 사진을 저장할 수 없습니다.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(this, "프로필 사진을 저장할 수 없습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            saveUserInfoToFIreStoreAndLogin(uid, email, password)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            PICK_IMAGE_FROM_ALBUM -> {
                if(resultCode == Activity.RESULT_OK) {
                    val profileImageUri = data?.data!!
                    profile_image.setImageURI(profileImageUri)
                    profileImage = profileImageUri.toString()
                }
            }
        }
    }

    fun saveUserInfoToFIreStoreAndLogin(uid: String, email: String, password: String) {
        // 회원 정보를 firestore database에 저장
        val phone = phoneNumber!!
        val userName = nickname_input.text.toString()
        val location = App.preference.location ?: "이건 null 일 수가 없어 - 오류발생~~!"
        val dataUser = DataUser(phone, userName, location, profileImage?:"defaultImageUri 나중에 추가")

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
    }
}
