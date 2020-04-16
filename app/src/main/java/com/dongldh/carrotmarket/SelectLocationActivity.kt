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
import kotlinx.android.synthetic.main.activity_select_location.*
import kotlinx.android.synthetic.main.item_select_location.view.*

class SelectLocationActivity : AppCompatActivity() {

    val locationList = mutableListOf<DataLocation>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)

        LocationRecyclerSetting()
    }

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

        locationList.sortWith(Comparator<DataLocation> { p0, p1 ->
            if(p0.name < p1.name) -1
            else if(p0.name == p1.name) 0
            else 1
        })

        result_recycler.layoutManager = LinearLayoutManager(this)
        result_recycler.adapter = ResultLocationAdapter(locationList)
    }

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

}
