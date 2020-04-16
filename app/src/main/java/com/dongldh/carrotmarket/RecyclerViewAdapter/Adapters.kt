package com.dongldh.carrotmarket.RecyclerViewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dongldh.carrotmarket.Database.DataLocation
import com.dongldh.carrotmarket.R
import kotlinx.android.synthetic.main.item_select_location.view.*

// 리사이클러뷰 어댑터의 기능을 분리하기 위해 만든 코틀린 파일



/*
        1.
        ResultLocation 관련 ViewHolder 및 Adapter
        SelectLocationActivity
*/

class ResultLocationViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val location = view.location_text
}

class ResultLocationAdapter(val list: MutableList<DataLocation>): RecyclerView.Adapter<ResultLocationViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultLocationViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ResultLocationViewHolder(layoutInflater.inflate(R.layout.item_select_location, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ResultLocationViewHolder, position: Int) {
        val item = list[position]

        holder.location.text = item.name
    }

}

// ========================================================================================================
// =========================================구분선==========================================================
// ========================================================================================================