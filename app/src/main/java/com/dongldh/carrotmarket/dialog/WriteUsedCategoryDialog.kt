package com.dongldh.carrotmarket.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.dongldh.carrotmarket.App
import com.dongldh.carrotmarket.R
import com.dongldh.carrotmarket.SignActivity
import kotlinx.android.synthetic.main.dialog_suggest_login.*
import kotlinx.android.synthetic.main.dialog_suggest_login.view.*
import kotlinx.android.synthetic.main.dialog_write_used_category.*
import kotlinx.android.synthetic.main.dialog_write_used_category.view.*

class WriteUsedCategoryDialog(val text: TextView): DialogFragment(), View.OnClickListener {
    var category = "카테고리"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_write_used_category, container, false)

        view.category_digital.setOnClickListener(this)
        view.category_furniture.setOnClickListener(this)
        view.category_baby.setOnClickListener(this)
        view.category_life_food.setOnClickListener(this)
        view.category_female_cloth.setOnClickListener(this)
        view.category_female.setOnClickListener(this)
        view.category_beauty.setOnClickListener(this)
        view.category_male.setOnClickListener(this)
        view.category_sport.setOnClickListener(this)
        view.category_game.setOnClickListener(this)
        view.category_book.setOnClickListener(this)
        view.category_animal.setOnClickListener(this)
        view.category_etc.setOnClickListener(this)
        view.category_buy.setOnClickListener(this)

        return view
    }

    override fun onClick(view: View) {
        // 받는 view가 공통적으로 TextView이므로 형변환!
        // 그 이후 category변수에 각 텍스트 값을 저장
        val textView = view as TextView
        category = textView.text.toString()
        
        // 액티비티에서 받아온 텍스트뷰에 텍스트를 설정
        text.text = category
        dismiss()
    }

    // 다이얼로그 크기 설정해주는 방법
    override fun onResume() {
        super.onResume()
        val windowManager = this.context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val params = dialog?.window?.attributes
        val deviceWidth = size.x
        val deviceHeight = size.y
        params?.width = (deviceWidth * 0.9).toInt()
        params?.height = (deviceHeight * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }



}