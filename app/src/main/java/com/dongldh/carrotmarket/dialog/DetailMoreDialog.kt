package com.dongldh.carrotmarket.dialog

import android.os.Bundle
import android.text.Layout
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dongldh.carrotmarket.R
import com.dongldh.carrotmarket.database.DataUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_detail_select.view.*
import kotlinx.android.synthetic.main.item_default_textview.view.*

class DetailMoreDialog: DialogFragment() {
    val list = mutableListOf<String>()

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(arguments?.getString("nowUserName") == arguments?.getString("uploadUserName")) {
            list.add("편집하기")
            list.add("삭제하기")
        } else {
            list.add("나중에 업데이트")
        }

        // 커스텀 다이얼로그 열면, 위에(top) 알 수 없는 여백이 생기는데, 이를 없애주는 코드는 아래와 같다.
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        // 다이얼로그 외부 영역 투명하게 만들기
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        // Toolbar (ActionBar)의 높이를 찾아주는 방법
        val attribute = activity?.theme?.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))!!
        val actionBarHeight = attribute.getDimension(0, 0.0F).toInt()
        attribute.recycle()

        // 커스텀 다이얼로그의 생성 위치를 결정해주는 작업
        val layoutParams = dialog?.window?.attributes!!
        layoutParams.gravity = Gravity.TOP or Gravity.END
        layoutParams.x = 16
        layoutParams.y = actionBarHeight - 24

        val view = inflater.inflate(R.layout.dialog_detail_select, container, false)
        isCancelable = true

        view.detail_select_recycler.layoutManager = LinearLayoutManager(this.context)
        view.detail_select_recycler.adapter = DetailMoreAdapter(list)

        return view
    }

    class DetailMoreViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val moreText = view.location_text
    }

    inner class DetailMoreAdapter(val list: MutableList<String>): RecyclerView.Adapter<DetailMoreViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailMoreViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return DetailMoreViewHolder(layoutInflater.inflate(R.layout.item_default_textview, null))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: DetailMoreViewHolder, position: Int) {
            val item = list[position]

            holder.moreText.text = item
        }

    }




}