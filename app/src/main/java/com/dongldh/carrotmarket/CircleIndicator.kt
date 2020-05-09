package com.dongldh.carrotmarket

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import android.widget.LinearLayout

// CircleIndicator 생성을 위해 만들어진 클래스
/*
    @param mContext = View 관련 클래스를 이용하려면 생성자 매개변수로 context를 넘겨줘야함
    @param mDefaultCircle = 선택되지 않은 사진일 경우의 인디케이터 이미지 id
    @param mSelectCircle = 선택된 사진일 경우의 인디케이터 이미지 id
    @param imageDot = 인디케이터 이미지를 담는 리스트 (갯수에 따라 사이즈가 달라짐)
 */
class CircleIndicator: LinearLayout {
    private var mContext: Context? = null
    private var mDefaultCircle = 0
    private var mSelectCircle = 0
    private var imageDot = mutableListOf<ImageView>()

    // 4.5dp를 픽셀로 바꿔줌. 인디케이터 사이에 padding을 주려고 만든 변수
    private val temp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.5f, resources.displayMetrics)

    constructor(context: Context): super(context) {
        mContext = context
    }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        mContext = context
    }

    // 인디케이터를 원하는 개수만큼 만들어서 imageDot 리스트에 넣어줌
    // 그 이후 LinearLayout에 뷰를 넣어준다.
    fun createDotPanel(count: Int, defaultCircle: Int, selectCircle: Int, position: Int) {
        this.removeAllViews()
        mDefaultCircle = defaultCircle
        mSelectCircle = selectCircle

        for(i in 0 until count) {
            imageDot.add(ImageView(mContext).apply{ setPadding(temp.toInt(), 0, temp.toInt(), 0) })
            this.addView(imageDot[i])
        }
        selectDot(position)
    }

    // position값에 해당 될 때 이미지 리소스를 바꾸어줌
    fun selectDot(position: Int) {
        for(i in imageDot.indices) {
            if(i == position) imageDot[i].setImageResource(mSelectCircle)
            else imageDot[i].setImageResource(mDefaultCircle)
        }
    }

}