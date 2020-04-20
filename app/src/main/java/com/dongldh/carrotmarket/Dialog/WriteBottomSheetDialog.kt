package com.dongldh.carrotmarket.Dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dongldh.carrotmarket.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WriteBottomSheetDialog: BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_write_bottom_sheet, container, false)
        return view
    }
}