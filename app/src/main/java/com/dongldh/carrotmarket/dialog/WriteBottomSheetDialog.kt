package com.dongldh.carrotmarket.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dongldh.carrotmarket.R
import com.dongldh.carrotmarket.write.WriteUsedActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_write_bottom_sheet.view.*

class WriteBottomSheetDialog: BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_write_bottom_sheet, container, false)

        view.used_transaction_layout.setOnClickListener {
            val intent = Intent(activity, WriteUsedActivity::class.java)
            intent.putExtra("location", arguments!!.getString("location"))
            intent.putExtra("locationNear", arguments!!.getString("locationNear"))
            startActivity(intent)
            dismiss()
        }

        return view
    }
}