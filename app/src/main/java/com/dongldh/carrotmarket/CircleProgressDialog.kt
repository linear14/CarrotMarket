package com.dongldh.carrotmarket

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Window

// CircleProgressDialog 생성! (인스턴스 만들자마자 바로 생성된다)
/***
        사용법
        생성 : val progress = CircleProgressDialog(context)
        종료 : progress.dismiss()
        간단쓰~~
 ***/
class CircleProgressDialog(context: Context) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE) // No Title
        setContentView(R.layout.dialog_circle_progress)

        // 기본 설정들
        this.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))   // 백그라운드 투명색
        this.setCancelable(false) // 다른 곳을 눌러도 종료되지 않도록
        this.show() // 인스턴스 생성 하자마자 보여주기
    }
}