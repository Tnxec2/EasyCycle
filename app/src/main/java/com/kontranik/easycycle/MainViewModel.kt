package com.kontranik.easycycle

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.kontranik.easycycle.constants.DefaultSettings
import com.kontranik.easycycle.database.DatabaseService
import com.kontranik.easycycle.database.sdfIso
import com.kontranik.easycycle.models.LastCycle
import com.kontranik.easycycle.models.Phase
import com.kontranik.easycycle.models.Settings
import com.kontranik.easycycle.models.StatisticItem
import com.kontranik.easycycle.storage.SettingsService
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class MainViewModel(val app: Application) : AndroidViewModel(app) {

    val settings = MutableLiveData<Settings>()
    val phases = MutableLiveData<List<Phase>>(listOf())
    val lastCycle = MutableLiveData<LastCycle>()
    val averageCycleLength = MutableLiveData<Int>(DefaultSettings.defaultCycleLength)

    private val listOfYearsStatistic: MutableList<StatisticItem> = mutableListOf()

    private var databaseService = DatabaseService(app)

    init {
        settings.value = SettingsService.loadSettings(app) ?: DefaultSettings.settings
        loadPhases()
        loadLastOneCycle()
    }

    fun saveSettings(newSettings: Settings) {
        SettingsService.saveSettings(newSettings, app)
        settings.value = newSettings
    }

    fun loadPredefindePhases() {
        SettingsService.removeCustomPhases(app)
        loadPhases()
    }

    private fun loadPhases() {
        val list = SettingsService.loadCustomPhases(app)

        if (list.isNotEmpty())  {
            phases.value = list.sortedWith(compareBy({ it.from }, { it.to }))
        } else {
            phases.value = listOf()
        }
    }

    fun removePhase(phase: Phase) {
        val newPhases = phases.value!!.filter { it.key != phase.key }
        SettingsService.saveCustomPhases(app, newPhases)
        phases.value = newPhases
    }

    fun savePhase(phase: Phase) {
        val newPhases = SettingsService.saveCustomPhase( app, phase)
        phases.value = newPhases.sortedWith(compareBy({ it.from }, { it.to }))
    }


    fun getListOfYearsStatistic(): MutableList<StatisticItem> {
        return listOfYearsStatistic
    }

    fun loadListOfYearsStatistic(yearsOnStatistic: Int) {
        listOfYearsStatistic.clear()
        val items = databaseService.getArchivList(yearsOnStatistic)
        if ( items.isNotEmpty() ) {
            listOfYearsStatistic.addAll(items)
        }
    }

    fun saveCycleItem(item: LastCycle) {
        databaseService.add(item)
        loadLastOneCycle()
    }

    private fun loadLastOneCycle() {
        lastCycle.value = databaseService.getLastOne()
        loadAverageLength()
    }

    private fun loadAverageLength() {
        val databaseAverageLength = databaseService.getAverageLength()
        if ( databaseAverageLength != null) {
            averageCycleLength.value = databaseAverageLength!!
        }
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
                    Log.d("EasyCycle", "set lastCycle...")
                    loadLastOneCycle()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteStatisticItemById(id: Long): Long {
        val deletedRows = databaseService.deleteById(id)
        loadLastOneCycle()
        return deletedRows
    }
}