package com.dongldh.carrotmarket.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dongldh.carrotmarket.R
import kotlinx.android.synthetic.main.fragment_show_item_detail.view.*

class DetailFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_show_item_detail, container, false)

        view.detail_item_info_title_text.text = arguments?.getString("title")

        return view
    }
}