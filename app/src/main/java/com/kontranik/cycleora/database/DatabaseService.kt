package com.kontranik.cycleora.database

import android.content.Context
import android.util.Log
import com.kontranik.cycleora.models.LastCycle
import com.kontranik.cycleora.models.StatisticItem
import java.util.*
import kotlin.collections.HashMap

class DatabaseService(context: Context) {
    private val adapter = DatabaseAdapter(context)

    fun getArchivList(yearsToLoad: Int): List<StatisticItem> {
        Log.d("DatabaseService", "getArchivList")
        adapter.open()
        val years: HashMap<Int, MutableList<LastCycle>> = hashMapOf()
        val calendar = Calendar.getInstance()

        adapter.getAllByYearsamount(yearsToLoad).forEach {
            var year = it.year
            calendar.time = it.cycleStart
            if ( year == null) year = calendar.get(Calendar.YEAR)
            if ( !years.containsKey(year)) {
                years[year] = mutableListOf()
            }
            years[year]!!.add(it)
        }
        val result: MutableList<StatisticItem> = mutableListOf()
        years.keys.forEach { key ->
            if ( years[key] != null) {
                var averageLengthSum = 0
                years[key]!!.forEach {
                    averageLengthSum += it.lengthOfLastCycle
                }
                result.add( StatisticItem(key.toString(), years[key]!!,
                    (averageLengthSum / years[key]!!.size)
                ))
            }
        }
        adapter.close()
        return result
    }

    fun getLastOne(): LastCycle? {
        adapter.open()
        val result = adapter.getLastOne()
        adapter.close()
        return result
    }

    fun add(lastCycle: LastCycle) {
        adapter.open()
        Log.d("DatabaseService", "add")
        adapter.insert(lastCycle)
        adapter.close()
    }

    fun deleteAll() {
        adapter.open()
        Log.d("DatabaseService", "deleteAll")
        adapter.deleteAll()
        adapter.close()
    }

    fun deleteById(id: Long): Long {
        adapter.open()
        Log.d("DatabaseService", "deleteById")
        val result = adapter.delete(id)
        adapter.close()
        return result
    }
}