package com.dongldh.carrotmarket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dongldh.carrotmarket.database.DBHelper
import com.dongldh.carrotmarket.database.DataLocation
import com.dongldh.carrotmarket.recycler_view_adapter.ResultLocationAdapter
import kotlinx.android.synthetic.main.activity_select_location.*

class SelectLocationActivity : AppCompatActivity() {

    // 전체 지역 리스트
    val locationList = mutableListOf<DataLocation>()
    // 검색창에 적힌 글자가 포함된 지역 리스트
    val searchedLocationList = mutableListOf<DataLocation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)

        LocationRecyclerSetting()

        clear_image.setOnClickListener { search_input.text.clear() }

        // 검색 창 글자 바뀔 때 마다 포함된 글자를 담는 리스트를 리사이클러뷰에 뿌려줄 수 있도록 설정
        search_input.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchedLocationList.clear()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0!!.length> 0) clear_image.visibility = View.VISIBLE
                else clear_image.visibility = View.GONE

                for(i in locationList) {
                    if(i.name.contains(p0)) searchedLocationList.add(i)
                }

                result_recycler.adapter = ResultLocationAdapter(searchedLocationList)

                // 검색 결과가 없다면? -> '결과가 없습니다' TextView를 보여주자.
                if(searchedLocationList.isEmpty()) no_result_text.visibility = View.VISIBLE
                else no_result_text.visibility = View.GONE
            }

        })
    }

    // 전체 location에 대하여 이름 오름차순으로 recyclerview에 뿌려주는 역할을 하는 메서드
    private fun LocationRecyclerSetting() {
        val helper = DBHelper(this)
        val db = helper.writableDatabase
        val cursor = db.rawQuery("select * from location", null)

        while(cursor.moveToNext()) {
            val name = cursor.getString(1)
            val row = cursor.getInt(2)
            val col = cursor.getInt(3)
            locationList.add(DataLocation(name, row, col))
        }

        cursor.close()
        db.close()

        // 이름을 기준으로 오름차순 정리
        locationList.sortWith(Comparator<DataLocation> { p0, p1 ->
            if(p0.name < p1.name) -1
            else if(p0.name == p1.name) 0
            else 1
        })

        result_recycler.layoutManager = LinearLayoutManager(this)
        result_recycler.adapter = ResultLocationAdapter(locationList)
    }
}
