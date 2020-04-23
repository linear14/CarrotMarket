package com.dongldh.carrotmarket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign.*
import java.util.*

class SignActivity : AppCompatActivity(), View.OnClickListener {
    // 인증문자 받기 버튼을 클릭했는지 아닌지의 여부
    var isClickVerifyMessageButton = false

    // 생성된 인증번호
    var verifyNumber: String = ""

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        send_verify_message_button.setOnClickListener(this)
        agree_and_start_button.setOnClickListener(this)

        // 인증문자 받기 버튼 활성화를 위한 작업 -> 핸드폰 글자 수가 10을 넘어야 활성화 됨
        phone_number_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.length >= 10) {
                    send_verify_message_button.isEnabled = true
                    send_verify_message_button.setBackgroundResource(R.drawable.decorate_button_active)
                } else {
                    send_verify_message_button.isEnabled = false
                    send_verify_message_button.setBackgroundResource(R.drawable.decorate_button_inactive)
                }
            }
        })

        // 동의하고 시작하기 버튼 활성화를 위한 작업 -> 인증번호가 4자리 입력 되어야 활성화 됨
        verify_number_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(isClickVerifyMessageButton && s!!.length == 4) {
                    agree_and_start_button.isEnabled = true
                    agree_and_start_button.setBackgroundResource(R.drawable.decorate_button_active)
                } else {
                    agree_and_start_button.isEnabled = false
                    agree_and_start_button.setBackgroundResource(R.drawable.decorate_button_inactive)
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when(v) {
            // 인증문자 받기 버튼일 경우
            send_verify_message_button -> {
                if(phone_number_input.text.toString().length >= 10) {
                    phone_number_input.isEnabled = false
                    send_verify_message_button.setText(R.string.resend_verify_message_button)
                    isClickVerifyMessageButton = true

                    // 문자를 보내는 기능은 돈이 들고, 현재는 구현을 할 수가 없으므로 우선은 Toast Message로 대체
                    makeVerifyNumber()
                    Toast.makeText(this, verifyNumber, Toast.LENGTH_LONG).show()
                }
            }

            // 동의하고 시작하기 버튼일 경우
            agree_and_start_button -> {
                if(isClickVerifyMessageButton && verify_number_input.text.toString().length == 4) {
                    if(verifyNumber == verify_number_input.text.toString()) {

                        // Login 검증 (이미 회원가입이 되어 있을 경우와 아닌경우)
                        auth = FirebaseAuth.getInstance()
                        val phoneNumber = phone_number_input.text.toString()
                        val email = "${phoneNumber}@test.com"
                        val password = "123456"

                        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                            if(task.isSuccessful) {
                                // 로그인이 성공한 경우(이미 핸드폰 번호가 존재하는 경우) -> 정상적으로 MainActivity로 이동
                                Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@SignActivity, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()
                            } else {
                                // 회원가입이 되어있지 않은 경우(등록되지 않은 핸드폰 번호) -> ProfileActivity로 이동해서 닉네임 설정하자.
                                Toast.makeText(this, "회원 등록 창으로 이동합니다.", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@SignActivity, ProfileActivity::class.java)
                                intent.putExtra("phoneNumber", phoneNumber)
                                startActivity(intent)
                                finish()
                            }
                        }
                    } else {
                        Toast.makeText(this, "인증번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    
    // 4자리 인증번호를 만드는 메서드
    fun makeVerifyNumber() {
        val random = Random()
        val verifyNumberBuilder = StringBuilder()
        for(i in 0 until 4) verifyNumberBuilder.append(random.nextInt(10))
        verifyNumber = verifyNumberBuilder.toString()
    }
}
