package com.dongldh.carrotmarket.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.dongldh.carrotmarket.App
import com.dongldh.carrotmarket.R
import com.dongldh.carrotmarket.SignActivity
import kotlinx.android.synthetic.main.dialog_suggest_login.*
import kotlinx.android.synthetic.main.dialog_suggest_login.view.*
import kotlinx.android.synthetic.main.dialog_write_community_category.view.*
import kotlinx.android.synthetic.main.dialog_write_used_category.*
import kotlinx.android.synthetic.main.dialog_write_used_category.view.*

class WriteCommunityCategoryDialog(val text: TextView): DialogFragment(), View.OnClickListener {
    var category = "카테고리"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        val view = inflater.inflate(R.layout.dialog_write_community_category, container, false)

        view.category_car.setOnClickListener(this)
        view.category_person.setOnClickListener(this)
        view.category_land.setOnClickListener(this)
        view.category_nature_food.setOnClickListener(this)
        view.category_company.setOnClickListener(this)
        view.category_study.setOnClickListener(this)
        view.category_party.setOnClickListener(this)

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
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }



}