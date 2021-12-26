package com.kontranik.easycycle.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

import android.database.DatabaseUtils

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.kontranik.easycycle.models.LastCycle
import java.text.SimpleDateFormat
import java.util.*

val sdfIso = SimpleDateFormat("yyyy-MM-dd")

class DatabaseAdapter(context: Context) {
    private val dbHelper: DatabaseHelper = DatabaseHelper(context.applicationContext)
    private var database: SQLiteDatabase? = null
    fun open(): DatabaseAdapter {
        database = dbHelper.writableDatabase
        return this
    }

    fun close() {
        dbHelper.close()
    }

    private fun allEntries(where: Pair<String, Array<String>>? = null): Cursor {
        if ( where != null) Log.d("EasyCycle", where.first + " " + where.second[0])
        val columns = arrayOf(
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_CYCLESTART,
            DatabaseHelper.COLUMN_YEAR,
            DatabaseHelper.COLUMN_MONTH,
            DatabaseHelper.COLUMN_LASTCYCLELENGTH
        )
        return database!!.query(
            DatabaseHelper.TABLE,
            columns,
            where?.first,
            where?.second,
            null,
            null,
            DatabaseHelper.COLUMN_CYCLESTART)
    }

    fun getAllByYearsamount(yearsToLoad: Int?): List<LastCycle> {
        val resultList: MutableList<LastCycle> = mutableListOf()
        var where: Pair<String, Array<String>>? = null
        if ( yearsToLoad != null) {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            val year = calendar.get(Calendar.YEAR) - yearsToLoad
            where = Pair( """${DatabaseHelper.COLUMN_YEAR} > ? """, arrayOf(year.toString()))
        }
        val cursor: Cursor = allEntries(where)
        while (cursor.moveToNext()) {
            resultList.add(getItemFromCursor(cursor))
        }
        cursor.close()
        return resultList
    }

    private fun getItemFromCursor(cursor: Cursor): LastCycle {
        var colIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)
        val id: Long = cursor.getLong(colIndex)
        colIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CYCLESTART)
        val cycleStartString: String = cursor.getString(colIndex)
        val cycleStart = sdfIso.parse(cycleStartString)
        colIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_YEAR)
        val year: Int = cursor.getInt(colIndex)
        colIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_MONTH)
        val month: Int = cursor.getInt(colIndex)
        colIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LASTCYCLELENGTH)
        val lastcyclelength: Int = cursor.getInt(colIndex)
        return LastCycle(id, year, month, cycleStart = cycleStart!!, lastcyclelength)
    }

    val count: Long
        get() = DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE)

    fun getOneByDate(lastCycleStartDate: Date): LastCycle? {
        var lastCycle: LastCycle? = null
        val searchDate = sdfIso.format(lastCycleStartDate)
        val query = String.format(
            "SELECT * FROM %s WHERE %s=?",
            DatabaseHelper.TABLE,
            DatabaseHelper.COLUMN_CYCLESTART
        )
        val cursor: Cursor = database!!.rawQuery(query, arrayOf(searchDate))
        if (cursor.moveToFirst()) {
            lastCycle = getItemFromCursor(cursor)
        }
        cursor.close()
        return lastCycle
    }

    fun getOneById(id: Long): LastCycle? {
        var lastCycle: LastCycle? = null
        val query = String.format(
            "SELECT * FROM %s WHERE %s=?",
            DatabaseHelper.TABLE,
            DatabaseHelper.COLUMN_ID
        )
        val cursor: Cursor = database!!.rawQuery(query, arrayOf(id.toString()))
        if (cursor.moveToFirst()) {
            lastCycle = getItemFromCursor(cursor)
        }
        cursor.close()
        return lastCycle
    }

    fun insert(item: LastCycle): Long {
        val cv = ContentValues()
        val calendar = Calendar.getInstance()
        calendar.time = item.cycleStart
        cv.put(DatabaseHelper.COLUMN_CYCLESTART, sdfIso.format(item.cycleStart))
        cv.put(DatabaseHelper.COLUMN_YEAR, calendar.get(Calendar.YEAR))
        cv.put(DatabaseHelper.COLUMN_MONTH, calendar.get(Calendar.MONTH))
        cv.put(DatabaseHelper.COLUMN_LASTCYCLELENGTH, item.lengthOfLastCycle)
        return database!!.insert(DatabaseHelper.TABLE, null, cv)
    }

    fun delete(id: Long): Long {
        val whereClause = "_id = ?"
        val whereArgs = arrayOf(id.toString())
        return database!!.delete(DatabaseHelper.TABLE, whereClause, whereArgs).toLong()
    }


    fun deleteAll(): Long {
        return database!!.delete(DatabaseHelper.TABLE, null, null).toLong()
    }

    fun getLastOne(): LastCycle? {
        var lastCycle: LastCycle? = null
        val query = String.format(
            """SELECT * FROM %s ORDER BY ${DatabaseHelper.COLUMN_CYCLESTART} DESC LIMIT 1""",
            DatabaseHelper.TABLE,
            DatabaseHelper.COLUMN_ID
        )
        val cursor: Cursor = database!!.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            lastCycle = getItemFromCursor(cursor)
        }
        cursor.close()
        return lastCycle
    }
}