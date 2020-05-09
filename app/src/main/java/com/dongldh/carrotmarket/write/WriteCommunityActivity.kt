package com.dongldh.carrotmarket.write

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dongldh.carrotmarket.R
import com.dongldh.carrotmarket.database.DataItem
import com.dongldh.carrotmarket.database.PICK_IMAGE_FROM_ALBUM
import com.dongldh.carrotmarket.database.Permissions
import com.dongldh.carrotmarket.dialog.WriteCommunityCategoryDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sign.*
import kotlinx.android.synthetic.main.activity_write_community.*
import kotlinx.android.synthetic.main.activity_write_community.back_image
import kotlinx.android.synthetic.main.activity_write_community.image_count_layout
import kotlinx.android.synthetic.main.activity_write_community.image_count_text
import kotlinx.android.synthetic.main.activity_write_community.next_text
import kotlinx.android.synthetic.main.item_write_photo_item.view.*

class WriteCommunityActivity : AppCompatActivity(), View.OnClickListener {
    var auth: FirebaseAuth? = null
    var fireStore: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null

    var location: String? = null
    var photoUriList = mutableListOf<Uri>()   // 받아온 사진 Uri
    var firebasePhotoUriList = arrayListOf<String>() // 사진 Uri를 string으로 바꿈 - 파이어베이스는 String형의 list만 허용 가능하므로

    var isPossibleChat = true   // 번호 입력 시 채팅 가능 여부 설정
    var counter = 0 // 업로드 성공, 혹은 실패 처리 된 이미지의 수를 카운팅.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_community)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        Permissions(applicationContext).permissionStorage()     // 내가 만든 Permissions 클래스

        countPhotos() // 등록된 사진의 갯수 수정
        // 아래에 있는 지역과 인접정보를 변경해준다.
        location = intent.getStringExtra("location")

        image_count_layout.setOnClickListener(this)
        write_community_category_layout.setOnClickListener(this)
        write_community_chat_check_layout.setOnClickListener(this)
        back_image.setOnClickListener(this)
        next_text.setOnClickListener(this)

        // 금액 입력 하면 WON 표시에 불 들어오게끔 ^^
        write_community_price_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isEmpty()) write_community_price_text.setTextColor(Color.parseColor("#cfcfcf"))
                else write_community_price_text.setTextColor(Color.BLACK)
            }
        })
    }

    // 상품의 예시 이미지를 imageView에 띄워주는 어댑터클래스
    // WriteUsedActivity와 같은 어댑터와 뷰홀더인데, 이것도 하나로 묶는 방법 없을까?
    class WriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.item_item_image
        val delete = view.item_delete_text
    }

    inner class WriteAdapter(val list: MutableList<Uri>) : RecyclerView.Adapter<WriteViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WriteViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return WriteViewHolder(
                layoutInflater.inflate(
                    R.layout.item_write_photo_item,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: WriteViewHolder, position: Int) {
            val uri = photoUriList[position]

            holder.image.setImageURI(uri)
            holder.delete.setOnClickListener {
                photoUriList.removeAt(position)
                countPhotos() // 등록된 사진의 갯수 수정
                write_community_recycler.adapter =
                    WriteAdapter(photoUriList)    // recyclerView 다시 업데이트 해야지!
            }
        }

    }

    override fun onClick(v: View?) {
        when (v) {
            // 이미지 삽입
            image_count_layout -> {
                if (photoUriList.size == 10) Toast.makeText(
                    this,
                    "사진을 더 이상 추가할 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                else {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, PICK_IMAGE_FROM_ALBUM)
                }
            }

            // category 설정 가능
            // Dialog 생성자로 textView를 집어 넣었기 때문에, 해당 클래스 안에서 text의 조작이 가능하게끔 설정
            write_community_category_layout -> {
                val dialog = WriteCommunityCategoryDialog(write_community_category_text)
                dialog.show(supportFragmentManager, "dialog_event")
            }

            // check를 누를 때 마다 이미지 변경 및 글자 색 변경
            // 그리고, data도 글과 관련된 정보 데이터도 바꿔 줘야 되는거 알지?
            write_community_chat_check_layout -> {
                if (isPossibleChat) {
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
            next_text -> uploadImages()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            // 갤러리에서 돌아온 뒤의 처리
            PICK_IMAGE_FROM_ALBUM -> {
                if (resultCode == Activity.RESULT_OK) {
                    val photoUri = data?.data!!
                    photoUriList.add(photoUri)      // UriList에 받아온 Uri값을 추가해준다.
                    countPhotos() // 등록된 사진의 갯수 수정

                    val layoutManager = LinearLayoutManager(this)
                    layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                    write_community_recycler.layoutManager = layoutManager
                    write_community_recycler.adapter = WriteAdapter(photoUriList)
                }
            }
        }
    }

    // 올라와있는 사진의 수를 센 뒤, 텍스트뷰를 재 설정해준다.
    fun countPhotos() {
        val count = photoUriList.size
        image_count_text.text =
            getString(R.string.write_community_image_count_text).replace("xx", count.toString())
    }

    // 저장된 정보를 firebase firestore에 저장하는 메서드
    fun uploadImages() {
        when {
            write_community_title_input.text.isEmpty() -> Toast.makeText(
                this,
                "제목을 입력해주세요",
                Toast.LENGTH_SHORT
            ).show()
            write_community_category_text.text.toString() == "카테고리" -> Toast.makeText(
                this,
                "카테고리를 선택해주세요",
                Toast.LENGTH_SHORT
            ).show()
            write_community_content_input.text.length < 15 -> Toast.makeText(
                this,
                "내용이 너무 짧습니다",
                Toast.LENGTH_SHORT
            ).show()
            write_community_price_input.text.toString().toInt() > 2000000000 -> Toast.makeText(
                this,
                "20억 이하의 가격을 입력해주세요",
                Toast.LENGTH_SHORT
            ).show()

            else -> {
                val uid = auth?.currentUser!!.uid

                // 이미지가 등록되어 있다면 이미지 포함하여 저장. 그렇지 않으면(else) 정보만 저장
                if (photoUriList.size != 0) {
                    val storageReference = storage?.reference
                    for (uri in photoUriList) {
                        val timestamp = System.currentTimeMillis()
                        val imageFileName = "IMAGE_${uid}_${timestamp}.png"

                        // storage에 이미지 저장
                        storageReference?.child("itemImages")?.child(imageFileName)?.putFile(uri)
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // 성공했을 경우, storage에 저장된 이미지를 다운 받을 수 있게 하는 url을 가져옴. 그 값을 리스트에 저장
                                    storageReference.child("itemImages").child(imageFileName)
                                        .downloadUrl.addOnCompleteListener { task2 ->
                                        counter++
                                        if (task2.isSuccessful) {
                                            firebasePhotoUriList.add(task2.result.toString())
                                        } else {
                                            // 만약 이미지는 저장이 됐지만 서버 오류등으로 이미지 url을 가져오지 못했다면, 이미지 삭제 및 토스트 메세지 띄움
                                            storageReference.child("itemImages")
                                                .child(imageFileName).delete()
                                            Toast.makeText(
                                                this@WriteCommunityActivity,
                                                "일부 사진을 저장할 수 없습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        if (counter == photoUriList.size) {
                                            saveImageToFireStore()
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        this@WriteCommunityActivity,
                                        "일부 사진을 업로드할 수 없습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    counter++
                                }
                            }
                    }
                } else saveImageToFireStore()
            }
        }
    }

    fun saveImageToFireStore() {
        val uid = auth?.currentUser!!.uid
        fireStore!!.collection("users").document(uid).get().addOnSuccessListener {
            val userName = it["userName"].toString()
            val type = 2
            val title = write_community_title_input.text.toString()
            val category = write_community_category_text.text.toString()
            val location = location!!
            val content = write_community_content_input.text.toString()
            val price = write_community_price_input.text.toString()
            val phone = if (phone_number_input == null) null else phone_number_input.text.toString()
            val isPossibleChat = isPossibleChat

            val item = DataItem(
                userName,
                type,
                title,
                category,
                location,
                content,
                if (price == "") null else price.toInt(),
                phone,
                photos = firebasePhotoUriList,
                possibleChat = isPossibleChat
            )

            fireStore!!.collection("UsedItems").document().set(item)
                .addOnSuccessListener {
                    Toast.makeText(this, "게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "게시글 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }

            finish()
        }
    }
}
