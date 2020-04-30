package com.dongldh.carrotmarket.database

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(context: Context) {
    val preference: SharedPreferences = context.getSharedPreferences("preference", 0)

    var location: String?
        get() = preference.getString("location", null)
        set(value) = preference.edit().putString("location", value).apply()
}