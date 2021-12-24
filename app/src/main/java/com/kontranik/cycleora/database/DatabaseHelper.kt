package com.kontranik.cycleora.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, SCHEMA) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE $TABLE ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COLUMN_CYCLESTART TEXT, $COLUMN_YEAR INTEGER, $COLUMN_MONTH INTEGER, $COLUMN_LASTCYCLELENGTH INTEGER);"""
        )

        /*db.execSQL(
            ("INSERT INTO " + TABLE + " (" + COLUMN_CYCLESTART
                    + ", " + COLUMN_YEAR + ") VALUES ('Том Смит', 1981);")
        )*/
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "cycleora.db"
        const val SCHEMA = 1
        const val TABLE = "cycles_archive"

        // названия столбцов
        const val COLUMN_ID = "_id"
        const val COLUMN_CYCLESTART = "cyclestart"
        const val COLUMN_YEAR = "year"
        const val COLUMN_MONTH = "month"
        const val COLUMN_LASTCYCLELENGTH = "last_cycle_length"
    }
}
