package com.dongldh.carrotmarket

import android.app.Application
import com.dongldh.carrotmarket.shared_preference.SharedPreference

class App : Application() {

    companion object {
        lateinit var preference: SharedPreference
    }

    override fun onCreate() {
        preference = SharedPreference(applicationContext)
        super.onCreate()
    }

}