package com.dongldh.carrotmarket.database

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(context: Context) {
    val preference: SharedPreferences = context.getSharedPreferences("preference", 0)

    var location: String?
        get() = preference.getString("location", null)
        set(value) = preference.edit().putString("location", value).apply()
    
    // 내 지역이 0번 인덱스로 설정 되어있는지 1번으로 설정 되어있는지 확인
    var nowSelected: Int
        get() = preference.getInt("now", 0)
        set(value) = preference.edit().putInt("now", value).apply()
}