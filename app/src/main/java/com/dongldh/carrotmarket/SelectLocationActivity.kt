package com.dongldh.carrotmarket

import android.Manifest
import android.content.Intent
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
import com.dongldh.carrotmarket.database.DBHelper
import com.dongldh.carrotmarket.database.DataLocation
import com.dongldh.carrotmarket.database.Permissions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_select_location.*
import kotlinx.android.synthetic.main.item_select_location.view.*
import java.util.ArrayList

class SelectLocationActivity : AppCompatActivity() {

    // 전체 지역 리스트
    val locationList = mutableListOf<DataLocation>()
    // 검색창에 적힌 글자가 포함된 지역 리스트
    val searchedLocationList = mutableListOf<DataLocation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)

        Permissions(applicationContext).permissionLocation()    // Permissions 클래스 안에 리스너랑 퍼미션설정하는 부분 다 구현함
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
                if(p0!!.length > 0) clear_image.visibility = View.VISIBLE
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

    // Adapter 설정
    class ResultLocationViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val location = view.location_text
    }

    inner class ResultLocationAdapter(val list: MutableList<DataLocation>): RecyclerView.Adapter<ResultLocationViewHolder>() {
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
            holder.itemView.setOnClickListener {
                App.preference.location = item.name
                val intent = Intent(this@SelectLocationActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
