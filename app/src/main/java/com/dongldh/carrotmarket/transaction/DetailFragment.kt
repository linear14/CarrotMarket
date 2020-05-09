package com.dongldh.carrotmarket.transaction

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.dongldh.carrotmarket.R
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_show_item_detail.*
import kotlinx.android.synthetic.main.fragment_show_item_detail.view.*
import kotlinx.android.synthetic.main.viewpager_detail_image.*

class DetailFragment: Fragment(), View.OnClickListener {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_show_item_detail, container, false)

        val userName = arguments?.getString("userName")
        val location = arguments?.getString("location")
        val title = arguments?.getString("title")
        val category = arguments?.getString("category")
        val time = arguments?.getString("time")
        val content = arguments?.getString("content")
        val type = arguments?.getInt("type")
        val price = arguments?.getInt("price")

        view.viewPager.adapter = ViewPagerAdapter()
        view.detail_profile_name.text = userName
        view.detail_profile_location.text = location
        view.detail_item_info_title_text.text = title
        view.detail_item_info_category_time_text.text = "$category · $time"
        view.detail_item_info_content_text.text = content

        if(type == 1) {
            if (price == 0) {
                view.detail_price_text.text = "무료나눔"
                view.detail_possible_suggestion_text.visibility = View.GONE
            } else {
                view.detail_price_text.text = "${String.format("%,d", price)}원"
                view.detail_possible_suggestion_text.text = if (arguments?.getBoolean("possibleSuggestion", true)!!) "가격제안 가능" else "가격제안 불가"
            }
        } else {
            if (price == 0) {
                view.detail_price_text.text = "가격없음"
            } else {
                view.detail_price_text.text = "${String.format("%,d", price)}원"
            }
            view.detail_possible_suggestion_text.visibility = View.GONE
        }

        view.detail_back_image.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?) {
        when(v) {
            detail_back_image -> {
                activity?.findViewById<View>(R.id.detail_content)?.visibility = View.GONE
                activity?.findViewById<View>(R.id.bottom_navigation)?.visibility = View.VISIBLE
            }
        }
    }


    // 이미지 슬라이더를 만들기 위한 뷰 페이저 어댑터
    inner class ViewPagerAdapter: PagerAdapter() {
        private var layoutInflater: LayoutInflater? = null
        val photos = arguments?.getStringArrayList("photos")

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getCount(): Int {
            return photos?.size?:0
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater!!.inflate(R.layout.viewpager_detail_image, null)
            val image = view.findViewById<ImageView>(R.id.detail_viewpager_imageview)

            Glide.with(context!!)
                .load(Uri.parse(photos!![position]))
                .into(image)

            // image!!.setImageURI(Uri.parse(photos!![position]))
            val viewPager = container as ViewPager
            viewPager.addView(view, 0)

            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val viewPager = container as ViewPager
            val view = `object` as View
            viewPager.removeView(view)
        }
    }
}
