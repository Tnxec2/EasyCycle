package com.kontranik.easycycle.ui.statistic

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.kontranik.easycycle.constants.DefaultSettings
import com.kontranik.easycycle.database.DatabaseService
import com.kontranik.easycycle.database.sdfIso
import com.kontranik.easycycle.models.LastCycle
import com.kontranik.easycycle.models.Settings
import com.kontranik.easycycle.models.StatisticItem
import com.kontranik.easycycle.storage.SettingsService
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class StatisticFragmentViewModel(val app: Application): AndroidViewModel(app) {
    private val listOfYears: MutableList<StatisticItem> = mutableListOf()

    private var databaseService = DatabaseService(app)

    fun getListOfYears(): MutableList<StatisticItem> {
        return listOfYears
    }

    fun loadListOfYears(yearsOnStatistic: Int) {
        listOfYears.clear()
        val items = databaseService.getArchivList(yearsOnStatistic)
        if ( items.isNotEmpty() ) {
            listOfYears.addAll(items)
        }
    }

    fun saveCycleItem(item: LastCycle) {
        databaseService.add(item)
    }

    fun getLastOneCycle(): LastCycle? {
        return databaseService.getLastOne()
    }

    fun importStatisticFromFile(fileUri: Uri?) {
        if ( fileUri != null) {
            try {
                val inputStream: InputStream? = app.contentResolver.openInputStream(fileUri)
                val r = BufferedReader(InputStreamReader(inputStream))
                var countSaved: Int = 0
                var line: String? = ""
                while (true) {
                    try {
                        if ( (r.readLine().also { line = it }) == null) break
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    var delimiter = ""
                    if ( line!!.contains(";")) delimiter = ";"
                    else if (line!!.contains(",")) delimiter = ","
                    if ( delimiter != "") {
                        val ar = line!!.split(delimiter)
                        if (ar.size >= 2) {
                            try {
                                val cycleStart = sdfIso.parse(ar[0])
                                val length = ar[1].toInt()
                                if (cycleStart != null) {
                                    val cycleItem = LastCycle(
                                        cycleStart = cycleStart,
                                        lengthOfLastCycle = length
                                    )
                                    saveCycleItem(cycleItem)
                                    countSaved += 1
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                r.close()
                inputStream?.close()

                Log.d("EasyCycle", "Imported items $countSaved")
                if ( countSaved > 0) {
                    val lastCycle = SettingsService.loadLastCycleStart(app)
                    if ( lastCycle == null) {
                        Log.d("EasyCycle", "set lastCycle...")
                        val lastOneCycle = getLastOneCycle()
                        if (lastOneCycle != null) {
                            SettingsService.saveLastCycleStart(
                                LastCycle(
                                    cycleStart = lastOneCycle.cycleStart,
                                    lengthOfLastCycle = lastOneCycle.lengthOfLastCycle)
                                , app
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}