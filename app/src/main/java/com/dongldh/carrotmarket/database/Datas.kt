package com.dongldh.carrotmarket.database

// geocoder등의 지역정보 API를 이용할 수 없어서, 임의의 지역 데이터를 지정하기 위해 만든 dataClass
data class DataLocation(val name: String, val row: Int, val col: Int)

// 회원정보(프로필) 저장
data class DataUser(var phone: String = "test", var userName: String = "로그인하세요", var location: String = "", var profileImage: String? = null, var locationNear: Long = 1L)

// 상품 등록 시 필요한 데이터 저장
// type : 1 -> 중고물품 거래,  2 -> 지역 홍보
data class DataItem(val userName: String, val type: Int, val title: String,
                    val category: String, val location: String, val content: String,
                    val price: Int? = null, val phone: String? = null,
                    val isPossibleSuggestion: Boolean? = null, val isPossibleChat: Boolean? = null,
                    val timeStamp: Long = System.currentTimeMillis())