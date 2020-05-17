package com.dongldh.carrotmarket.main_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dongldh.carrotmarket.R
import com.dongldh.carrotmarket.database.DBHelper
import com.dongldh.carrotmarket.database.DataItem
import com.dongldh.carrotmarket.nestedFragmentState
import com.dongldh.carrotmarket.transaction.DetailFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_uploaded_item.view.*

class HomeFragment: Fragment() {
    val auth: FirebaseAuth? = FirebaseAuth.getInstance()
    val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    var myLocation: String? = null
    var myLocationNear: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        myLocation = arguments?.getString("location")
        myLocationNear = arguments?.getString("locationNear")?.toInt()

        view.main_recycler.layoutManager = LinearLayoutManager(activity)
        view.main_recycler.adapter = MainAdapter()
        return view
    }

    class MainViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val photo = view.featured_image
        val title = view.title_text
        val location = view.location_text
        val price = view.price_text
    }

    inner class MainAdapter: RecyclerView.Adapter<MainViewHolder>() {
        var itemList = mutableListOf<DataItem>()

        init {
            fireStore.collection("UsedItems").orderBy("timeStamp", Query.Direction.DESCENDING).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException != null) { return@addSnapshotListener }
                itemList.clear()

                // 가까운 동네 리스트를 받아와서, 동네 이름이 그 조건에 만족하는 항목만 가져온다.
                val closeLocationList = findNearLocation(myLocation!!, myLocationNear!!)

                for(snapshot in querySnapshot!!.documents) {
                    val item = snapshot?.toObject(DataItem::class.java)
                    if(item!!.location in closeLocationList) {
                        itemList.add(item)
                    }
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return MainViewHolder(layoutInflater.inflate(R.layout.item_uploaded_item, parent, false))
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
            val item = itemList[position]

            // 게시글 올린 시간과 현재 시간과의 차이를 문자열로 바꾸어줌
            val time: String
            when(val timePassed = System.currentTimeMillis() - item.timeStamp) {
                in 0 until 60 * 1000 -> time = "방금 전"
                in 60 * 1000 until 60 * 60 * 1000 -> time = "${timePassed / 60000}분 전"
                in 60 * 60 * 1000 until 24 * 60 * 60 * 1000 -> time = "${timePassed / 3600000}시간 전"
                else -> time = "${timePassed / (24 * 3600000)}일 전"
            }

            // 저장된 사진이 없다면 기본 이미지를 등록, 있다면 사진 모음의 첫 사진을 등록
            if(item.photos.isNullOrEmpty()) holder.photo.setImageResource(R.mipmap.ic_launcher)
            else {
                Glide.with(holder.itemView.context)
                    .load(item.photos[0])
                    .into(holder.photo)
            }

            holder.title.text = item.title
            holder.location.text = getString(R.string.item_location_text)
                .replace("xx", item.location!!)
                .replace("yy", time)

            if(item.price == 0) holder.price.text = "무료나눔"
            else holder.price.text = getString(R.string.item_price_text).replace("xx", String.format("%,d", item.price))

            holder.itemView.setOnClickListener {
                nestedFragmentState = true
                val fragment = DetailFragment()
                val bundle = Bundle()

                bundle.putString("title", item.title)
                bundle.putString("userName", item.userName)
                bundle.putString("location", item.location)
                bundle.putString("category", item.category)
                bundle.putString("time", time)
                bundle.putString("content", item.content)
                bundle.putInt("price", item.price ?: 0)
                bundle.putInt("type", item.type!!)
                bundle.putStringArrayList("photos", item.photos)

                if(item.type == 1) bundle.putBoolean("possibleSuggestion", item.possibleSuggestion!!)
                else bundle.putBoolean("possibleChat", item.possibleChat!!)

                fragment.arguments = bundle

                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.detail_content, fragment)?.commit()
                activity?.findViewById<View>(R.id.detail_content)?.visibility = View.VISIBLE
                activity?.findViewById<View>(R.id.bottom_navigation)?.visibility = View.GONE
            }
        }
    }

    // 가까운 동네 리스트를 반환
    fun findNearLocation(location: String, locationNear: Int): MutableList<String> {
        val list = mutableListOf<String>()
        val helper = DBHelper(activity?.applicationContext!!)
        val db = helper.writableDatabase

        val cursor = db.rawQuery("select * from location where name='${location}'", null)
        cursor.moveToNext()

        val row = cursor.getInt(2)
        val col = cursor.getInt(3)

        val newCursor = db.rawQuery("select * from location where abs(_row-$row) + abs(col-$col) <= $locationNear", null)
        while(newCursor.moveToNext()) {
            list.add(newCursor.getString(1))
        }

        db.close()
        helper.close()

        return list
    }
}