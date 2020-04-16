package com.dongldh.carrotmarket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dongldh.carrotmarket.Database.DBHelper
import com.dongldh.carrotmarket.Database.DataLocation
import com.dongldh.carrotmarket.RecyclerViewAdapter.ResultLocationAdapter
import kotlinx.android.synthetic.main.activity_select_location.*
import kotlinx.android.synthetic.main.item_select_location.view.*

class SelectLocationActivity : AppCompatActivity() {

    val locationList = mutableListOf<DataLocation>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)

        LocationRecyclerSetting()
    }

    // 전체 location을 이름 오름차순으로 recyclerview에 뿌려주는 역할을 하는 메서드
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
