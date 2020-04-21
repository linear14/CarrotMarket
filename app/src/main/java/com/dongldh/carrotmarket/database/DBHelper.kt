package com.dongldh.carrotmarket.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context): SQLiteOpenHelper(context, "carrotDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createLocationTable = "create table location " +
                "(num integer primary key autoincrement, name, _row, col)"
        db!!.execSQL(createLocationTable)

        db.execSQL("insert into location (name, _row, col) values ('사과', 1, 1)")
        db.execSQL("insert into location (name, _row, col) values ('과일', 1, 2)")
        db.execSQL("insert into location (name, _row, col) values ('고라니', 1, 3)")
        db.execSQL("insert into location (name, _row, col) values ('정보', 1, 4)")
        db.execSQL("insert into location (name, _row, col) values ('족보', 1, 5)")
        db.execSQL("insert into location (name, _row, col) values ('서장', 1, 6)")
        db.execSQL("insert into location (name, _row, col) values ('정수기', 2, 1)")
        db.execSQL("insert into location (name, _row, col) values ('아이스크림', 2, 2)")
        db.execSQL("insert into location (name, _row, col) values ('폭탄', 2, 3)")
        db.execSQL("insert into location (name, _row, col) values ('마을', 2, 4)")
        db.execSQL("insert into location (name, _row, col) values ('과목', 2, 5)")
        db.execSQL("insert into location (name, _row, col) values ('건강', 2, 6)")
        db.execSQL("insert into location (name, _row, col) values ('효도', 3, 1)")
        db.execSQL("insert into location (name, _row, col) values ('효도르', 3, 2)")
        db.execSQL("insert into location (name, _row, col) values ('은하수', 3, 3)")
        db.execSQL("insert into location (name, _row, col) values ('전기', 3, 4)")
        db.execSQL("insert into location (name, _row, col) values ('말차', 3, 5)")
        db.execSQL("insert into location (name, _row, col) values ('관광', 3, 6)")
        db.execSQL("insert into location (name, _row, col) values ('을지로', 4, 1)")
        db.execSQL("insert into location (name, _row, col) values ('키위', 4, 2)")
        db.execSQL("insert into location (name, _row, col) values ('자두', 4, 3)")
        db.execSQL("insert into location (name, _row, col) values ('어미새', 4, 4)")
        db.execSQL("insert into location (name, _row, col) values ('트럭', 4, 5)")
        db.execSQL("insert into location (name, _row, col) values ('황천길', 4, 6)")
        db.execSQL("insert into location (name, _row, col) values ('혹시', 5, 1)")
        db.execSQL("insert into location (name, _row, col) values ('다리미', 5, 2)")
        db.execSQL("insert into location (name, _row, col) values ('도전', 5, 3)")
        db.execSQL("insert into location (name, _row, col) values ('표범', 5, 4)")
        db.execSQL("insert into location (name, _row, col) values ('트와이스', 5, 5)")
        db.execSQL("insert into location (name, _row, col) values ('해발', 5, 6)")
        db.execSQL("insert into location (name, _row, col) values ('모발', 6, 1)")
        db.execSQL("insert into location (name, _row, col) values ('두피', 6, 2)")
        db.execSQL("insert into location (name, _row, col) values ('탈모', 6, 3)")
        db.execSQL("insert into location (name, _row, col) values ('코틀린', 6, 4)")
        db.execSQL("insert into location (name, _row, col) values ('누나', 6, 5)")
        db.execSQL("insert into location (name, _row, col) values ('철수', 6, 6)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table t_travel")
        onCreate(db)
    }

}