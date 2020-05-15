package com.dongldh.carrotmarket.database

const val FROM_WRITE_TO_SETTING_LOCATION = 1000 // WriteUsedActivity -> SettingLocationActivity -> WriteUsedActivity
const val FROM_CHANGE_LOCATION_TO_SETTING_LOCATION = 1010 // ChangeLocationDialog -> SettingLocationActivity -> MainActivity
const val PICK_IMAGE_FROM_ALBUM = 2000 // 앨범에서 사진 가져오기