package com.dongldh.carrotmarket.write

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.dongldh.carrotmarket.R
import com.dongldh.carrotmarket.database.DataItem
import com.dongldh.carrotmarket.dialog.WriteCommunityCategoryDialog
import com.dongldh.carrotmarket.dialog.WriteUsedCategoryDialog
import com.dongldh.carrotmarket.setting.SettingLocationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign.*
import kotlinx.android.synthetic.main.activity_write_community.*
import kotlinx.android.synthetic.main.activity_write_used.*
import kotlinx.android.synthetic.main.activity_write_used.back_image
import kotlinx.android.synthetic.main.activity_write_used.next_text
import kotlinx.android.synthetic.main.activity_write_used.title_text
import kotlinx.android.synthetic.main.item_uploaded_item.*

class WriteCommunityActivity : AppCompatActivity(), View.OnClickListener {
    var auth: FirebaseAuth? = null
    var fireStore: FirebaseFirestore? = null

    var location: String? = null

    var isPossibleChat = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_community)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        // 아래에 있는 지역과 인접정보를 변경해준다.
        location = intent.getStringExtra("location")

        write_community_category_layout.setOnClickListener(this)
        write_community_chat_check_layout.setOnClickListener(this)
        back_image.setOnClickListener(this)
        next_text.setOnClickListener(this)

        // 금액 입력 하면 WON 표시에 불 들어오게끔 ^^
        write_community_price_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.isEmpty()) write_community_price_text.setTextColor(Color.parseColor("#cfcfcf"))
                else write_community_price_text.setTextColor(Color.BLACK)
            }
        })
    }

    override fun onClick(v: View?) {
        when(v) {
            // category 설정 가능
            // Dialog 생성자로 textView를 집어 넣었기 때문에, 해당 클래스 안에서 text의 조작이 가능하게끔 설정
            write_community_category_layout -> {
                val dialog = WriteCommunityCategoryDialog(write_community_category_text)
                dialog.show(supportFragmentManager, "dialog_event")
            }

            // check를 누를 때 마다 이미지 변경 및 글자 색 변경
            // 그리고, data도 글과 관련된 정보 데이터도 바꿔 줘야 되는거 알지?
            write_community_chat_check_layout -> {
                if(isPossibleChat) {
                    isPossibleChat = false
                    write_community_chat_check_image.setImageResource(R.drawable.ic_checked)
                    write_community_chat_check.setTextColor(Color.BLACK)
                } else {
                    isPossibleChat = true
                    write_community_chat_check_image.setImageResource(R.drawable.ic_unchecked)
                    write_community_chat_check.setTextColor(Color.parseColor("#B6B6B6"))
                }
            }

            back_image -> finish()
            next_text -> uploadItem()

        }
    }

    // 저장된 정보를 firebase firestore에 저장하는 메서드
    fun uploadItem() {
        when {
            write_community_title_input.text.isEmpty() -> Toast.makeText(this, "제목을 입력해주세요", Toast.LENGTH_SHORT).show()
            write_community_category_text.text.toString() == "카테고리" -> Toast.makeText(this, "카테고리를 선택해주세요", Toast.LENGTH_SHORT).show()
            write_community_content_input.text.length < 15 -> Toast.makeText(this, "내용이 너무 짧습니다", Toast.LENGTH_SHORT).show()

            else -> {
                val uid = auth?.currentUser!!.uid

                // 접속 성공 리스너를 달아줘야함. 비동기적으로 작동을 시키기 위해서임
                fireStore!!.collection("users").document(uid).get().addOnSuccessListener {
                    val userName = it["userName"].toString()
                    val type = 2
                    val title = write_community_title_input.text.toString()
                    val category = write_community_category_text.text.toString()
                    val location = location!!
                    val content = write_community_content_input.text.toString()
                    val price = write_community_price_input.text.toString()
                    val phone= if(phone_number_input == null) null else phone_number_input.text.toString()
                    val isPossibleChat = isPossibleChat

                    val item = DataItem(userName, type, title, category, location, content, if(price == "") null else price.toInt(), phone , isPossibleChat = isPossibleChat)
                    fireStore!!.collection("UsedItems").document().set(item)
                        .addOnSuccessListener { Toast.makeText(this, "게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show() }
                        .addOnFailureListener { Toast.makeText(this, "게시글 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show() }

                    finish()
                }
            }
        }
    }
}
