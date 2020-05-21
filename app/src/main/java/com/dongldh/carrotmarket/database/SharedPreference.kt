package com.dongldh.carrotmarket.database

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONException

class SharedPreference(context: Context) {
    val preference: SharedPreferences = context.getSharedPreferences("preference", 0)

    var location: String?
        get() = preference.getString("location", null)
        set(value) = preference.edit().putString("location", value).apply()
    
    // 내 지역이 0번 인덱스로 설정 되어있는지 1번으로 설정 되어있는지 확인
    var nowSelected: Int
        get() = preference.getInt("now", 0)
        set(value) = preference.edit().putInt("now", value).apply()

    // https://codechacha.com/ko/sharedpref_arraylist/
    // 어레이리스트를 SharedPreference로 저장하기 (저장할떄는 json 꼴의 string으로 저장) (받아올 때는 json을 arraylist로 변환)
    // 메인에서 보여지지 않을 카테고리를 담는 어레이리스트
    var notSelectedCategory: ArrayList<String>
        get() {
            val json = preference.getString("category", null)
            val arrayList = ArrayList<String>()
            if(json != null) {
                try {
                    val a = JSONArray(json)
                    for(i in 0 until a.length()) {
                        arrayList.add(a.optString(i))
                    }
                } catch(e: JSONException) {
                    e.printStackTrace()
                }
            }
            return arrayList
        }
        set(values) {
            val editor = preference.edit()
            val a = JSONArray()
            for(i in 0 until values.size) {
                a.put(values[i])
            }
            if(values.isNotEmpty()) {
                editor.putString("category", a.toString())
            } else {
                editor.putString("category", null)
            }
            editor.apply()
        }

}