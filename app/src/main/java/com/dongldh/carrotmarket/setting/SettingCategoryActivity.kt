package com.dongldh.carrotmarket.setting

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dongldh.carrotmarket.App
import com.dongldh.carrotmarket.R
import kotlinx.android.synthetic.main.activity_setting_category.*
import kotlinx.android.synthetic.main.item_category_setting.view.*

class SettingCategoryActivity : AppCompatActivity(), View.OnClickListener {

    val notSelectedCategory = App.preference.notSelectedCategory
    val originalNameList = arrayListOf("디지털/가전", "가구/인테리어", "유아동/유아도서", "생활/가공식품",
        "스포츠/레저", "여성잡화", "여성의류", "남성패션/잡화", "게임/취미", "뷰티/미용", "반려동물용품", "도서/티켓/음반",
        "기타 중고물품", "삽니다")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_category)

        category_setting_recycler.layoutManager = GridLayoutManager(this, 2)
        category_setting_recycler.adapter = SettingCategoryAdapter(originalNameList)
        back_image.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            back_image -> {
                App.preference.notSelectedCategory = notSelectedCategory
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    class SettingCategoryViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val categorySettingLayout = view.category_setting_layout
        val categorySettingImage = view.category_setting_check
        val categorySettingText = view.category_setting_text
    }

    inner class SettingCategoryAdapter(val list: ArrayList<String>): RecyclerView.Adapter<SettingCategoryViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingCategoryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return SettingCategoryViewHolder(layoutInflater.inflate(R.layout.item_category_setting, parent, false))
        }

        override fun getItemCount(): Int {
            return originalNameList.size
        }

        override fun onBindViewHolder(holder: SettingCategoryViewHolder, position: Int) {
            val item = list[position]

            if(item in notSelectedCategory) holder.categorySettingImage.setImageResource(R.drawable.ic_unchecked)
            holder.categorySettingText.text = item

            holder.categorySettingLayout.setOnClickListener {
                if(item in notSelectedCategory) {
                    notSelectedCategory.remove(item)
                    holder.categorySettingImage.setImageResource(R.drawable.ic_checked)
                } else {
                    if(notSelectedCategory.size == 13) {
                        Toast.makeText(this@SettingCategoryActivity, "최소 1개 이상 선택되어 있어야 합니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        notSelectedCategory.add(item)
                        holder.categorySettingImage.setImageResource(R.drawable.ic_unchecked)
                    }
                }
            }
        }

    }
}
