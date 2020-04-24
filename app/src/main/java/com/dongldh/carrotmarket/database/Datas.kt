package com.dongldh.carrotmarket.database

// geocoder등의 지역정보 API를 이용할 수 없어서, 임의의 지역 데이터를 지정하기 위해 만든 dataClass
data class DataLocation(val name: String, val row: Int, val col: Int)

// 회원정보(프로필) 저장
data class DataUser(val phone: String, var userName: String, var location: String, var profileImage: String? = null)